package com.tfg.review.services.implementations;

import com.tfg.review.dtos.FullSLRDTO;
import com.tfg.review.dtos.ResearcherDTO;
import com.tfg.review.dtos.SLRDTO;
import com.tfg.review.dtos.UserDTO;
import com.tfg.review.exceptions.CantDeleteException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.SLRNotFoundException;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.Rol;
import com.tfg.review.models.SLR;
import com.tfg.review.repositories.SLRRepository;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.services.ResearcherService;
import com.tfg.review.services.SLRService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;


@Service
@Transactional //means that if a transaction of multiple actions is not completed entirely, all actions are undone.
@AllArgsConstructor
@Slf4j
public class SLRServiceImpl implements SLRService {
    private final SLRRepository slrRepository;
    private final ProtocolService protocolService;
    private final ResearcherService researcherService;

    //--Basic CRUD methods--//
    @Override
    public SLR findOne(Long slrId){

        return slrRepository.findSLRById(slrId).orElseThrow(() ->  new SLRNotFoundException(MessageConstants.NULL_SLR));
    }

    @Override
    public List<SLR> findAll(){

        return slrRepository.findAll();
    }

    @Override
    public SLRDTO createAndSave(SLRDTO slr, Long principalResearcherId){
        if(slr==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        if(principalResearcherId == null) throw new IllegalArgumentException(MessageConstants.PRINCIPAL_NOT_FOUND);

        Researcher principal = researcherService.findOne(principalResearcherId);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -10);

        SLR result = SLRDTO.buildEntity(slr);
        result.setInitDate(calendar.getTime());
        result.setPublicVisibility(true);

        if(result.getResearchers()==null||result.getResearchers().isEmpty()) {
            result.setResearchers(new HashSet<>());
        }

        result.addResearcher(principal);

        researcherService.update(principal.getId());
        slrRepository.save(result);

        return SLRDTO.buildFromEntity(result);
    }

    public FullSLRDTO update (SLR slr){
        if(slr==null|| !slrRepository.existsById(slr.getId())) throw new SLRNotFoundException(MessageConstants.NULL_SLR);

        return FullSLRDTO.buildFromEntity(slrRepository.save(slr));
    }

    @Override
    public void delete(Long slrId){
        SLR slr = slrRepository.findSLRById(slrId).orElseThrow(() -> new NullEntityException(MessageConstants.NULL_SLR));
        Protocol relatedProtocol = slr.getProtocol();
        try {
            protocolService.delete(relatedProtocol.getId());
            slrRepository.deleteSLRById(slrId);
        }catch (Exception e){
            throw new CantDeleteException(MessageConstants.ERROR_DELETING);
        }
    }

    //--Assistant methods--//
    @Override
    public List<SLR> findByResearcherUserId(Long userId){
        if(userId==null) throw new IllegalArgumentException(MessageConstants.USER_ID_IS_NULL);
        List<Researcher> researcherList = researcherService.findByUserId(userId);
        List<SLR> result = new ArrayList<>();
        for(Researcher researcher : researcherList) {
            result.addAll( researcher.getSLRs());
        }
        return result;
    }

    public Researcher addResearcher(Long slrId, UserDTO researcherDTO, Long userId){
        if(researcherDTO == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);

        SLR slr = findOne(slrId);
        Researcher researcher = researcherService.createFromUser(researcherDTO, userId);
        researcher.setRol(Rol.COLLABORATOR);
        researcherService.update(researcher.getId());

        slr.addResearcher(researcher);
        this.update(slr);

        return researcher;
    }
}

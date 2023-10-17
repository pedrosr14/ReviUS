package com.tfg.review.services.implementations;

import com.tfg.review.dtos.ResearcherDTO;
import com.tfg.review.dtos.UserDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ResearcherNotFoundException;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.Rol;
import com.tfg.review.repositories.ResearcherRepository;
import com.tfg.review.services.ResearcherService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ResearcherServiceImpl implements ResearcherService {
    private final ResearcherRepository researcherRepository;


    public List<Researcher> findAll(){
        return researcherRepository.findAll();
    }

    public Researcher findOne(Long id){
        return researcherRepository.findResearcherById(id).orElseThrow(() -> new ResearcherNotFoundException(MessageConstants.RESEARCHER_NOT_FOUND));
    }

    public List<Researcher> findByUserId(Long id){
        if(id==null) throw new NullEntityException(MessageConstants.USER_ID_IS_NULL);

        return List.copyOf(researcherRepository.findAllByUserId(id));
    }

    public Researcher createFromUser(UserDTO user, Long userId){
        if(user == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        if(userId == null) throw new NullEntityException(MessageConstants.USER_ID_NULL);
        Researcher result = new Researcher(null, user.getName(), Rol.PRINCIPAL, userId, new HashSet<>());
        return researcherRepository.save(result);
    }

    public Researcher update(Long researcherId){
        Researcher researcherUpdate = researcherRepository.findResearcherById(researcherId)
                .orElseThrow(()-> new ResearcherNotFoundException(MessageConstants.RESEARCHER_NOT_FOUND));
        return researcherRepository.save(researcherUpdate);
    }
}

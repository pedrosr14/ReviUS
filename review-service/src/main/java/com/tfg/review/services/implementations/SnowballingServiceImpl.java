package com.tfg.review.services.implementations;

import com.tfg.review.dtos.SnowballingDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ReportNotFoundException;
import com.tfg.review.exceptions.SnowballingNotFoundException;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.Snowballing;
import com.tfg.review.models.SnowballingType;
import com.tfg.review.repositories.SnowballingRepository;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.services.SnowballingService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class SnowballingServiceImpl implements SnowballingService {

    private final SnowballingRepository snowballingRepository;
    private final ProtocolService protocolService;

    //--CRUD--//

    public List<Snowballing> findAll(){

        return snowballingRepository.findAll();
    }

    public Optional<Snowballing> findOne(Long id){

        return snowballingRepository.findSnowballingById(id);
    }

    public SnowballingDTO createAndSave(SnowballingDTO dto, Long protocolId){
        if(dto == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        Protocol protocol = protocolService.findOne(protocolId);
        Snowballing result = SnowballingDTO.buildEntity(dto);
            result.setName(dto.getName());

            if (dto.getSnowballingType().equals("BACKWARDS")){
                result.setSnowballingType(SnowballingType.BACKWARDS);
            } else if (dto.getSnowballingType().equals("FORWARD")) {
                result.setSnowballingType(SnowballingType.FORWARD);
            }

        result.setProtocols(new HashSet<>());
        snowballingRepository.save(result);

        protocol.addDataSource(result);
        protocolService.update(protocol);
        return SnowballingDTO.buildFromEntity(result);
    }

    public Snowballing update(Snowballing snowballing){
        if(snowballing==null) throw new NullEntityException(MessageConstants.USER_ID_IS_NULL);
        if(!snowballingRepository.existsById(snowballing.getId())) throw new SnowballingNotFoundException(MessageConstants.SNOWBALLING_NOT_FOUND);

        return snowballingRepository.save(snowballing);
    }

    public void deleteFromProtocol(Long snowballingId, Long protocolId){
        Snowballing snowballing = snowballingRepository.findSnowballingById(snowballingId).orElseThrow(()->new SnowballingNotFoundException(MessageConstants.SNOWBALLING_NOT_FOUND));
        Protocol protocol = protocolService.findOne(protocolId);

        protocol.removeDataSource(snowballing);
        protocolService.update(protocol);
    }
}

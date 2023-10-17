package com.tfg.review.services.implementations;

import com.tfg.review.dtos.CustomDataSourceDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.CustomDataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.CustomDataSourceRepository;

import com.tfg.review.services.CustomDataSourceService;
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
public class CustomDataSourceServiceImpl implements CustomDataSourceService {

    private final CustomDataSourceRepository customRepository;
    private final ProtocolServiceImpl protocolService;

    //--CRUD--//

    public List<CustomDataSource> findAll(){

        return customRepository.findAll();
    }

    public Optional<CustomDataSource> findOne(Long id){

        return customRepository.findCustomDataSourceById(id);
    }

    public CustomDataSourceDTO createAndSave(CustomDataSourceDTO dataSource, Long protocolId){
        if(dataSource==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        Protocol protocol = protocolService.findOne(protocolId);

        CustomDataSource result = CustomDataSourceDTO.buildEntity(dataSource);
            result.setName(dataSource.getName());
            result.setProtocols(new HashSet<>());

        result.addProtocol(protocol);
        customRepository.save(result);

        protocol.addDataSource(result);
        protocolService.update(protocol);
        return CustomDataSourceDTO.buildFromEntity(result);
    }

    public CustomDataSource update(CustomDataSource dataSource){
        if(!customRepository.existsById(dataSource.getId())) throw new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND);

        return customRepository.save(dataSource);
    }

    public void deleteFromProtocol(Long dataSourceId, Long protocolId){
        CustomDataSource customDataSource = customRepository.findCustomDataSourceById(dataSourceId).orElseThrow(()->new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND));
        Protocol protocol = protocolService.findOne(protocolId);

        protocol.removeDataSource(customDataSource);
        protocolService.update(protocol);
    }
}

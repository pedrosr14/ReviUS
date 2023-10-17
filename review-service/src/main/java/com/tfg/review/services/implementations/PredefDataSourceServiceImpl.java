package com.tfg.review.services.implementations;

import com.tfg.review.dtos.PredefDataSourceDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.PredefDataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.PredefDataSourceRepository;
import com.tfg.review.services.PredefDataSourceService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PredefDataSourceServiceImpl implements PredefDataSourceService {

    private final PredefDataSourceRepository predefRepository;

    private final ProtocolServiceImpl protocolService;

    //--CRUD--//

    public List<PredefDataSource> findAll(){

        return predefRepository.findAll();
    }

    public Optional<PredefDataSource> findOne(Long id){

        return predefRepository.findPredefDataSourceById(id);
    }

    public PredefDataSourceDTO createAndSave(PredefDataSourceDTO dataSource, Long protocolId){
        if(dataSource==null) throw new NullEntityException(MessageConstants.DATA_SOURCE_NOT_FOUND);
        Protocol protocol = protocolService.findOne(protocolId);

        PredefDataSource result = PredefDataSourceDTO.buildEntity(dataSource);
            result.setName(dataSource.getName());
            result.setUrl(dataSource.getUrl());
            result.setProtocols(new HashSet<>());

        result.addProtocol(protocol);
        predefRepository.save(result);

        protocol.addDataSource(result);
        protocolService.update(protocol);

        return PredefDataSourceDTO.buildFromEntity(result);
    }

    public PredefDataSource update(PredefDataSource dataSource){
        if(!predefRepository.existsById(dataSource.getId())) throw new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND);

        return predefRepository.save(dataSource);
    }

    public void deleteFromProtocol(Long dataSourceId, Long protocolId){
        PredefDataSource predefDataSource = predefRepository.findPredefDataSourceById(dataSourceId).orElseThrow(()->new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND));
        Protocol protocol = protocolService.findOne(protocolId);

        protocol.removeDataSource(predefDataSource);
        protocolService.update(protocol);
    }

}

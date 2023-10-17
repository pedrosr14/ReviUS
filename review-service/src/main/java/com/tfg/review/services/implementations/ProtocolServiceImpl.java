package com.tfg.review.services.implementations;

import com.tfg.review.dtos.FullProtocolDTO;
import com.tfg.review.dtos.ProtocolDTO;
import com.tfg.review.exceptions.*;
import com.tfg.review.models.*;
import com.tfg.review.repositories.*;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class ProtocolServiceImpl implements ProtocolService {

    private final ProtocolRepository protocolRepository;
    private final SelectionCriteriaRepository selectionCriteriaRepository;
    private final KeywordRepository keywordRepository;
    private final CustomDataSourceRepository customDataSourceRepository;
    private final PredefDataSourceRepository predefDataSourceRepository;
    private final SnowballingRepository snowballingRepository;
    private final RestTemplate restTemplate;
    private final SLRRepository sLRRepository;

    //--CRUD--//

    public List<Protocol> findAll(){

        return protocolRepository.findAll();
    }

    public Protocol findOne(Long protocolId){

       return protocolRepository.findProtocolById(protocolId).orElseThrow(() -> new ProtocolNotFoundException(MessageConstants.PROTOCOL_NOT_FOUND));
    }

    public FullProtocolDTO createAndSave(ProtocolDTO protocolDTO, Long slrId){
        SLR slr = sLRRepository.findSLRById(slrId).orElseThrow(()-> new SLRNotFoundException(MessageConstants.NULL_SLR));
        if(protocolDTO == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        Protocol protocol = ProtocolDTO.buildEntity(protocolDTO);

        slr.setProtocol(protocol);
        protocol.setSlr(slr);
        protocol.setKeywords(new HashSet<>());
        protocol.setDataSources(new HashSet<>());
        protocol.setSelectionCriteria(new HashSet<>());
        protocolRepository.save(protocol);
        sLRRepository.save(slr);

        return FullProtocolDTO.buildFromEntity(protocol);
    }

    public Protocol update (Protocol protocol){
        if(!protocolRepository.existsById(protocol.getId())) throw new ProtocolNotFoundException(MessageConstants.PROTOCOL_NOT_FOUND);

        return protocolRepository.save(protocol);
    }

    public void delete(Long protocolId){
        Protocol protocol = protocolRepository.findProtocolById(protocolId).orElseThrow(() -> new ProtocolNotFoundException(MessageConstants.PROTOCOL_NOT_FOUND));
        SLR father = protocol.getSlr();

        father.setProtocol(null);
        sLRRepository.save(father);

        //Need to be an array list to iterate and modify
        for(SelectionCriteria selectionCriteria: List.copyOf(protocol.getSelectionCriteria())){
            selectionCriteria.removeProtocol(protocol);
            selectionCriteriaRepository.save(selectionCriteria);
        }

        for(Keyword keyword: List.copyOf(protocol.getKeywords())){
            keyword.removeProtocol(protocol);
            keywordRepository.save(keyword);
        }

        for(DataSource dataSource: List.copyOf(protocol.getDataSources())){
            if(dataSource instanceof CustomDataSource){
                dataSource.removeProtocol(protocol);
                customDataSourceRepository.save((CustomDataSource) dataSource);
            }else if(dataSource instanceof PredefDataSource) {
                dataSource.removeProtocol(protocol);
                predefDataSourceRepository.save((PredefDataSource) dataSource);
            } else if(dataSource instanceof Snowballing) {
                snowballingRepository.save((Snowballing) dataSource);
            }
        }
        //Forms have a composition relationship and they should delete automatically
        if(protocol.getExtractionForm()!= null){
            ResponseEntity<String> response = restTemplate.exchange("http://search-service/api/form-instance/full-delete/{formId}",
                    HttpMethod.DELETE, null, String.class, protocol.getExtractionForm().getId());

            if (response.getStatusCodeValue() == 500){
                throw new ServiceDownException(MessageConstants.SERVICE_DOWN);
            } else if (response.getStatusCodeValue() != 200){
                throw new IllegalArgumentException(MessageConstants.ERROR_DELETING);
            }

        }
        if(protocol.getQualityForm()!= null){
            ResponseEntity<String> response = restTemplate.exchange("http://search-service/api/form-instance/full-delete/{formId}",
                    HttpMethod.DELETE, null, String.class, protocol.getQualityForm().getId());

                if (response.getStatusCodeValue() == 500){
                    throw new ServiceDownException(MessageConstants.SERVICE_DOWN);
                } else if (response.getStatusCodeValue() != 200){
                    throw new IllegalArgumentException(MessageConstants.ERROR_DELETING);
                }
        }
    }

    //--Assistant methods--//
    public List<SelectionCriteria> findSelectionCriteriaFromSearch(Long protocolId){

        Protocol protocol = protocolRepository.findProtocolById(protocolId).orElseThrow(()-> new ProtocolNotFoundException(MessageConstants.PROTOCOL_NOT_FOUND));
        Set<SelectionCriteria> selectionCriteria = protocol.getSelectionCriteria();

        return new ArrayList<SelectionCriteria>(selectionCriteria);
    }
}

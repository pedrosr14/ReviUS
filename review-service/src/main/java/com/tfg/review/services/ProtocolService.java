package com.tfg.review.services;

import com.tfg.review.dtos.FullProtocolDTO;
import com.tfg.review.dtos.ProtocolDTO;
import com.tfg.review.models.*;

import java.util.List;


public interface ProtocolService {

    List<Protocol> findAll();
    Protocol findOne(Long protocolId);
    FullProtocolDTO createAndSave(ProtocolDTO protocolDTO, Long slrId);
    Protocol update (Protocol protocol);
    void delete(Long protocolId);

    //--Assistant methods--//
    List<SelectionCriteria> findSelectionCriteriaFromSearch(Long protocolId);
}

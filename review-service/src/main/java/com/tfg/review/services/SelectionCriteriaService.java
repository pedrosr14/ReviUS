package com.tfg.review.services;

import com.tfg.review.dtos.SelectionCriteriaDTO;
import com.tfg.review.models.SelectionCriteria;

import java.util.List;
import java.util.Optional;

public interface SelectionCriteriaService {

     List<SelectionCriteria> findAll();
     Optional<SelectionCriteria> findOne(Long id);
     List<SelectionCriteria> getAppliedCriteria(List<Long> appliedCriteriaIds);
     SelectionCriteriaDTO createAndSave(SelectionCriteriaDTO criteria, Long protocolId);
     SelectionCriteriaDTO addCriteria(SelectionCriteriaDTO criteria, Long protocolId) ;
     SelectionCriteria update(SelectionCriteria criteria);
     void deleteFromProtocol(Long selectionCriteriaId, Long protocolId);
     void delete(Long selectionCriteriaId);
}

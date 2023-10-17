package com.tfg.review.services.implementations;

import com.tfg.review.dtos.SelectionCriteriaDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.SelectionCriteriaNotFoundException;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.SelectionCriteria;
import com.tfg.review.repositories.SelectionCriteriaRepository;
import com.tfg.review.services.SelectionCriteriaService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@AllArgsConstructor
@Service
public class SelectionCriteriaServiceImpl implements SelectionCriteriaService {

    private final SelectionCriteriaRepository selectionCriteriaRepository;
    private final ProtocolServiceImpl protocolService;

    //--CRUD--//

    public List<SelectionCriteria> findAll() {

        return selectionCriteriaRepository.findAll();
    }

    public Optional<SelectionCriteria> findOne(Long id) {
        if (id == null || !selectionCriteriaRepository.existsById(id))
            throw new SelectionCriteriaNotFoundException(MessageConstants.NULL_ID_ENTITY);

        return selectionCriteriaRepository.findSelectionCriteriaById(id);
    }

    @Override
    public List<SelectionCriteria> getAppliedCriteria(List<Long> appliedCriteriaIds) {
        return selectionCriteriaRepository.findSelectionCriteriaByIdIn(appliedCriteriaIds);
    }

    public SelectionCriteriaDTO createAndSave(SelectionCriteriaDTO criteria, Long protocolId) {
        if (criteria == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        Protocol father = protocolService.findOne(protocolId);
        SelectionCriteria result;
        if(criteria.getId()!=null){
            result = selectionCriteriaRepository.findSelectionCriteriaById(criteria.getId())
                    .orElseThrow(() -> new SelectionCriteriaNotFoundException(MessageConstants.SELECTION_CRITERIA_NOT_FOUND));
        }else{
            result = SelectionCriteriaDTO.buildEntity(criteria);
        }
        result.addProtocol(father);
        result = selectionCriteriaRepository.save(result);
        father.addSelectionCriteria(result);
        protocolService.update(father);

        return SelectionCriteriaDTO.buildFromEntity(result);
    }

    public SelectionCriteriaDTO addCriteria(SelectionCriteriaDTO criteria, Long protocolId) {
        if (criteria == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        Protocol father = protocolService.findOne(protocolId);
        SelectionCriteria result;
        Set<SelectionCriteria> oldCriteria = father.getSelectionCriteria();

        if(criteria.getId()==null){
            result = SelectionCriteriaDTO.buildEntity(criteria);
        }else{
            result = selectionCriteriaRepository.findSelectionCriteriaById(criteria.getId())
                    .orElseThrow(() -> new SelectionCriteriaNotFoundException(MessageConstants.SELECTION_CRITERIA_NOT_FOUND));
        }
        result.addProtocol(father);
        if(oldCriteria.contains(result)) {
            result = selectionCriteriaRepository.save(result);
            father.addSelectionCriteria(result);
        }
        protocolService.update(father);

        return SelectionCriteriaDTO.buildFromEntity(result);
    }

    public SelectionCriteria update(SelectionCriteria criteria) {
        if (criteria == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        if (!selectionCriteriaRepository.existsById(criteria.getId()))
            throw new SelectionCriteriaNotFoundException(MessageConstants.SELECTION_CRITERIA_NOT_FOUND);

        return selectionCriteriaRepository.save(criteria);
    }

    public void deleteFromProtocol(Long selectionCriteriaId, Long protocolId) {
        if (selectionCriteriaId == null || protocolId == null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);

        SelectionCriteria criteria = selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId).orElseThrow(()-> new SelectionCriteriaNotFoundException("Selection Criteria doesn't exist"));
        Protocol protocol = protocolService.findOne(protocolId);

        protocol.removeSelectionCriteria(criteria);
        protocolService.update(protocol);
    }

    public void delete(Long selectionCriteriaId){
        if (selectionCriteriaId == null) throw new IllegalArgumentException("Selection Criteria ID is null");
        SelectionCriteria criteria = selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId).orElseThrow(()-> new SelectionCriteriaNotFoundException("Selection Criteria doesn't exist"));
        List<Protocol> protocols = List.copyOf(criteria.getProtocols());

        for(Protocol protocol: protocols){
            protocol.removeSelectionCriteria(criteria);
            protocolService.update(protocol);
        }

        selectionCriteriaRepository.delete(criteria);
    }

    public void getAppliedCriteria(Long protocolId, List<Long> appliedCriteriaIds){
        if(protocolId == null) throw new SelectionCriteriaNotFoundException(MessageConstants.NULL_ID_ENTITY);


    }

    //--Assistant methods--//

}
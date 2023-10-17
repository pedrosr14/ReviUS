package com.tfg.slr.searchservice.services.implementations;

import com.google.common.collect.Lists;
import com.tfg.slr.searchservice.dtos.SearchDTO;
import com.tfg.slr.searchservice.dtos.SelectionCriteriaDTO;
import com.tfg.slr.searchservice.dtos.StudyDTO;
import com.tfg.slr.searchservice.exceptions.NullEntityException;
import com.tfg.slr.searchservice.exceptions.StudyNotFoundException;
import com.tfg.slr.searchservice.models.Search;
import com.tfg.slr.searchservice.models.Status;
import com.tfg.slr.searchservice.models.Study;
import com.tfg.slr.searchservice.repositories.StudyRepository;
import com.tfg.slr.searchservice.services.SearchService;
import com.tfg.slr.searchservice.services.StudyService;
import com.tfg.slr.searchservice.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class StudyServiceImpl implements StudyService {

    private StudyRepository studyRepository;
    private SearchService searchService;
    private RestTemplate restTemplate;

    public List<Study> findAll(){
        return studyRepository.findAll();
    }

    public Study findOne(Long id){
        return studyRepository.findStudyById(id).orElseThrow(()-> new StudyNotFoundException(MessageConstants.STUDY_NOT_FOUND));
    }

    public Study createAndSave(Long searchId, StudyDTO dto){
        if(dto == null) throw new IllegalArgumentException(MessageConstants.NULL_ENTITY_ID);
        Search search = searchService.findOne(searchId);

        Study result = StudyDTO.buildEntity(dto);
        if(result.getYear() > Year.now().getValue()){
            throw new IllegalArgumentException("Studies can't come from the future!");
        }
        result.setPriority(null);
        result.setScore(0);
        result.setSelectionStatus(Status.UNCLASSIFIED);
        result.setExtractionStatus(Status.UNCLASSIFIED);
        result.setSearch(search);

        Study response  = studyRepository.save(result);
        search.getStudies().add(response);
        searchService.update(search);
        return response;
    }

    public StudyDTO update(Study study){
        if(study == null) throw new NullEntityException(MessageConstants.NULL_ENTITY_ID);
        if(!studyRepository.existsById(study.getId())) throw new StudyNotFoundException(MessageConstants.STUDY_NOT_FOUND);

        Study result = studyRepository.save(study);

        return StudyDTO.buildFromEntity(result);
    }

    public List<SelectionCriteriaDTO> getSelectionCriteria(Long studyId){
        Study study = this.findOne(studyId);
        Search search = study.getSearch();

        return restTemplate.getForObject("http://review-service:8002/api/review/protocol/"+search.getProtocolId()+"/get-selection-criteria", List.class);
    }

    public List<SelectionCriteriaDTO> getAppliedSelectionCriteria(Long studyId){
        Study study = this.findOne(studyId);
        List<Long> appliedCriteriaIds = Lists.newArrayList(study.getAppliedCriteriaIds());

        return restTemplate.getForObject("http://review-service:8002/api/selection-criteria/get-applied-criteria", List.class, appliedCriteriaIds);
    }

    public void applySelectionCriteria (Long studyId, Set<Long> criteriaApplied){
        Study study = this.findOne(studyId);
        Set<Long> currentCriteriaList = study.getAppliedCriteriaIds();
        currentCriteriaList.addAll(criteriaApplied);
        study.setAppliedCriteriaIds(currentCriteriaList);
    }

    public void removeSelectionCriteria (Long studyId, Long criteriaId){
        Study study = this.findOne(studyId);
        Set<Long> currentCriteriaList = study.getAppliedCriteriaIds();
        currentCriteriaList.remove(criteriaId);
        study.setAppliedCriteriaIds(currentCriteriaList);
    }

    public StudyDTO acceptStudy(Long studyId){
        Study study = this.findOne(studyId);
        study.setSelectionStatus(Status.ACCEPTED);
        return StudyDTO.buildFromEntity(studyRepository.save(study));
    }

    public StudyDTO rejectStudy(Long studyId){
        Study study = this.findOne(studyId);
        study.setSelectionStatus(Status.REJECTED);
        studyRepository.save(study);
        return StudyDTO.buildFromEntity(studyRepository.save(study));
    }

    public StudyDTO markAsDuplicated(Long studyId){
        Study study = this.findOne(studyId);
        study.setSelectionStatus(Status.DUPLICATED);
        studyRepository.save(study);
        return StudyDTO.buildFromEntity(studyRepository.save(study));
    }

}

package com.tfg.slr.searchservice.services;

import com.tfg.slr.searchservice.dtos.SelectionCriteriaDTO;
import com.tfg.slr.searchservice.dtos.StudyDTO;
import com.tfg.slr.searchservice.exceptions.StudyNotFoundException;
import com.tfg.slr.searchservice.models.Search;
import com.tfg.slr.searchservice.models.Status;
import com.tfg.slr.searchservice.models.Study;

import java.time.Year;
import java.util.List;

public interface StudyService {

    Study findOne(Long id);

    List<Study> findAll();

    Study createAndSave(Long searchId, StudyDTO dto);

    StudyDTO update(Study study);

    List<SelectionCriteriaDTO> getSelectionCriteria(Long studyId);

    StudyDTO acceptStudy(Long studyId);

    StudyDTO rejectStudy(Long studyId);

    StudyDTO markAsDuplicated(Long studyId);


}

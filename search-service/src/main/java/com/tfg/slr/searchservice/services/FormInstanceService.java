package com.tfg.slr.searchservice.services;

import com.tfg.slr.searchservice.dtos.FormInstanceDTO;
import com.tfg.slr.searchservice.models.FormInstance;
import com.tfg.slr.searchservice.models.FormType;

import java.util.List;

public interface FormInstanceService {

    FormInstance findOne(Long id);

    List<FormInstance> findAll();

    FormInstanceDTO createAndSave(FormType formType, Long formID);

    FormInstanceDTO update(FormInstance formInstance);

    void delete(Long formInstanceId);

    void deleteFromProtocol(Long formId);
}

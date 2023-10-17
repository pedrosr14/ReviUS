package com.tfg.review.services;

import com.tfg.review.dtos.FormFieldDTO;
import com.tfg.review.models.Form;
import com.tfg.review.models.FormType;


import java.util.List;
import java.util.Optional;


public interface FormService {

    Optional<Form> findOne(Long formId);

    List<Form> findAll();
    List<FormFieldDTO> findFormWithFields(Long protocolId, FormType formType);

    Form createAndSave(Form form, Long protocolId);

    Form update(Form form);

    void delete(Form form);
}

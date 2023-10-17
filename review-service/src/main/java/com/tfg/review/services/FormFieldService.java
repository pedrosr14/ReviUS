package com.tfg.review.services;

import com.tfg.review.models.FormField;

import java.util.List;
import java.util.Optional;

public interface FormFieldService {

    List<FormField> findAll();

    Optional<FormField> findOne(Long id);

    FormField createAndSave(FormField field, Long formId);

    FormField update(FormField field);

    void delete(Long id);
}

package com.tfg.review.services.implementations;

import com.tfg.review.exceptions.FormFieldNotFoundException;
import com.tfg.review.exceptions.FormNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.Form;
import com.tfg.review.models.FormField;
import com.tfg.review.repositories.FormFieldRepository;
import com.tfg.review.services.FormFieldService;
import com.tfg.review.services.FormService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class FormFieldServiceImpl implements FormFieldService {

    private final FormFieldRepository formFieldRepository;
    private final FormService formService;


    //--CRUD--//

    public List<FormField> findAll() {

        return formFieldRepository.findAll();
    }

    public Optional<FormField> findOne(Long id) {
        if(id==null) throw new FormNotFoundException("FormField ID does not exist");

        return formFieldRepository.findFormFieldById(id);
    }

    public FormField createAndSave(FormField field, Long formId){
        if(field == null) throw new NullEntityException ("Form field is null");
        Form form = formService.findOne(formId).orElseThrow(()-> new IllegalArgumentException("Related Form not found"));
        field.setForm(form);
        FormField result = formFieldRepository.save(field);
        form.getFormFields().add(result);
        formService.update(form);
        return result;
    }

    public FormField update(FormField field){
        if(field==null) throw new NullEntityException("FormField is null");
        if(!formFieldRepository.existsFormFieldById(field.getId())) throw new FormFieldNotFoundException("Form field not found");

        return formFieldRepository.save(field);
    }

    public void delete(Long id){
        if(id==null) throw new IllegalArgumentException("ID is null");

        FormField field = formFieldRepository.findFormFieldById(id).orElseThrow(()-> new FormFieldNotFoundException("Form field doesn't exist"));

        Form relatedForm = field.getForm();

        formFieldRepository.delete(field);
        formService.update(relatedForm);
    }

    //--Other methods--//
}

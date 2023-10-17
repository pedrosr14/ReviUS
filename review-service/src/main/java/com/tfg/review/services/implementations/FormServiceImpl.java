package com.tfg.review.services.implementations;

import com.tfg.review.dtos.FormDTO;
import com.tfg.review.dtos.FormFieldDTO;
import com.tfg.review.exceptions.FormFieldNotFoundException;
import com.tfg.review.exceptions.FormNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.Form;
import com.tfg.review.models.FormField;
import com.tfg.review.models.FormType;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.FormRepository;
import com.tfg.review.services.FormService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    private final ProtocolServiceImpl protocolService;
    //--CRUD--//

    public Optional<Form> findOne(Long formId){
        if(formId==null) throw new FormNotFoundException(MessageConstants.FORM_NOT_FOUND);

        return formRepository.findFormById(formId);
    }

    public List<Form> findAll() {

        return formRepository.findAll();
    }

    public List<FormFieldDTO> findFormWithFields(Long protocolId, FormType formType){
        if(formType == null) throw new IllegalArgumentException(MessageConstants.NULL_ID_ENTITY);
        Protocol protocol = protocolService.findOne(protocolId);
        Form form = null;
        if(formType.equals(FormType.QUALITY)){
            form = protocol.getQualityForm();
        }else if(formType.equals(FormType.EXTRACTION)){
            form = protocol.getExtractionForm();
        }

        if (form == null) {
            throw new FormNotFoundException(MessageConstants.FORM_NOT_FOUND);
        }
        if(form.getFormFields().isEmpty()){
            throw new FormFieldNotFoundException(MessageConstants.FORM_FIELDS_NOT_FOUND);
        }
        return form.getFormFields().stream().map(FormFieldDTO::buildFromEntity).collect(Collectors.toList());
    }

    public Form createAndSave(Form form, Long protocolId){
        if(form==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);

        Protocol father = protocolService.findOne(protocolId);

        if(form.getFormType()== FormType.EXTRACTION){
            father.setExtractionForm(form);
            form.setProtocolToExtraction(father);
        }else if(form.getFormType()==FormType.QUALITY){
            father.setQualityForm(form);
            form.setProtocolToQuality(father);
        }

        formRepository.save(form);
        protocolService.update(father);

        return form;
    }

    public Form update(Form form){
        if(form==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        return formRepository.save(form);
    }

    public void delete(Form form){
        if(form==null) throw new NullEntityException(MessageConstants.FORM_NOT_FOUND);

        Protocol relatedProtocol = new Protocol();

        if(form.getFormType()==FormType.QUALITY){
            relatedProtocol = form.getProtocolToQuality();
            relatedProtocol.setQualityForm(null);
        } else if (form.getFormType()== FormType.EXTRACTION){
            relatedProtocol = form.getProtocolToQuality();
            relatedProtocol.setExtractionForm(null);
        }

        formRepository.delete(form);
        protocolService.update(relatedProtocol);
    }
    //--Assistant methods--//
}

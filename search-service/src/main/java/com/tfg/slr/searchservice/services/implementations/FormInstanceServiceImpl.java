package com.tfg.slr.searchservice.services.implementations;

import com.tfg.slr.searchservice.dtos.FormFieldInstanceDTO;
import com.tfg.slr.searchservice.dtos.FormInstanceDTO;
import com.tfg.slr.searchservice.exceptions.FormInstanceNotFoundException;
import com.tfg.slr.searchservice.exceptions.NullEntityException;
import com.tfg.slr.searchservice.models.FormFieldInstance;
import com.tfg.slr.searchservice.models.FormInstance;
import com.tfg.slr.searchservice.models.FormType;
import com.tfg.slr.searchservice.models.Study;
import com.tfg.slr.searchservice.repositories.FormFieldInstanceRepository;
import com.tfg.slr.searchservice.repositories.FormInstanceRepository;
import com.tfg.slr.searchservice.services.FormInstanceService;
import com.tfg.slr.searchservice.services.StudyService;
import com.tfg.slr.searchservice.utils.MessageConstants;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class FormInstanceServiceImpl implements FormInstanceService {

    private FormInstanceRepository formInstanceRepository;
    private StudyService studyService;
    private FormFieldInstanceRepository formFieldInstanceRepository;
    private RestTemplate restTemplate;

    public FormInstance findOne(Long id){
        if(id == null) throw new NullEntityException(MessageConstants.NULL_ENTITY_ID);
        return formInstanceRepository.findFormInstanceById(id).orElseThrow(()-> new FormInstanceNotFoundException(MessageConstants.FORM_INSTANCE_NOT_FOUND));
    }

    public List<FormInstance> findAll(){

        return formInstanceRepository.findAll();
    }

    public FormInstanceDTO createAndSave(FormType formType, Long studyId){
        if(studyId == null) throw new NullEntityException(MessageConstants.NULL_ENTITY_ID);

        Study relatedStudy = studyService.findOne(studyId);
        Long protocolID = relatedStudy.getSearch().getProtocolId();

        FormInstance formInstance = FormInstance.builder().formType(formType).build();

        if(formType.equals(FormType.EXTRACTION)){
            relatedStudy.setExtractionFormInstance(formInstance);
        } else if (formType.equals(FormType.QUALITY)){
            relatedStudy.setQualityFormInstance(formInstance);
        }

        FormInstance result = formInstanceRepository.save(formInstance);
        List<FormFieldInstanceDTO> DtoList= restTemplate.getForObject("http://review-service:8002/api/review/protocol/"
                +protocolID+"/get-form-data/"+formType, List.class);

        Set<FormFieldInstance> fields = new HashSet<>();
        for(FormFieldInstanceDTO fieldInstanceDTO : DtoList){
            FormFieldInstance fieldInstance = FormFieldInstance.builder().name(fieldInstanceDTO.getFieldName()).build();
            fieldInstance.setFormInstance(formInstance);
            formFieldInstanceRepository.save(fieldInstance);
            fields.add(fieldInstance);
        }

        result.setFields(fields);
        formInstanceRepository.save(result);

        studyService.update(relatedStudy);

        return FormInstanceDTO.builder().id(result.getId()).formId(result.getFormId()).formType(result.getFormType()).build();
    }


    public FormInstanceDTO update(FormInstance formInstance) {
        return FormInstanceDTO.builder().build();
    }

    public void delete(Long formInstanceId){

    }

    public void deleteFromProtocol(Long formId){
        FormInstance formInstance = formInstanceRepository.findFormInstanceByFormId(formId)
                .orElseThrow(() -> new FormInstanceNotFoundException(MessageConstants.FORM_INSTANCE_NOT_FOUND));

        formInstanceRepository.delete(formInstance);
    }
}

package com.tfg.review.dtos;

import com.tfg.review.models.Form;
import com.tfg.review.models.FormType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FullFormDTO {

    private Long protocolId;
    private Long id;

    @NotBlank
    private String formType;

    private List<String> formFields = new ArrayList<>();

    public static FullFormDTO buildFromEntity (Form form){
        FullFormDTO result = FullFormDTO.builder().id(form.getId()).formType(form.getFormType().toString()).build();
        if(form.getFormType().equals(FormType.EXTRACTION)){
            result.setFormType("EXTRACTION");
            result.setProtocolId(form.getProtocolToExtraction().getId());
        } else if (form.getFormType().equals(FormType.QUALITY)) {
            result.setFormType("QUALITY");
            result.setProtocolId(form.getProtocolToQuality().getId());
        }

        List<String> fields = form.getFormFields().stream().map(formField -> formField.getFieldName()+" ("+formField.getFieldType()+")").collect(Collectors.toList());

        result.setFormFields(fields);
        return result;
    }

}

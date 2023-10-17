package com.tfg.review.dtos;

import com.tfg.review.models.FormField;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormFieldDTO {

    private Long id;

    private Long formId;

    @NotBlank
    private String fieldName;

    @NotNull
    private String fieldType;

    public static FormFieldDTO buildFromEntity(FormField formField) {
        return FormFieldDTO.builder().id(formField.getId()).formId(formField.getForm().getId()).fieldName(formField.getFieldName())
                .fieldType(formField.getFieldType()).build();
    }
}

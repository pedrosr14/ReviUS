package com.tfg.review.dtos;

import com.tfg.review.models.Form;
import com.tfg.review.models.FormType;
import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormDTO {

    private Long id;

    @NotNull
    private FormType formType;

    private Long protocolId;

    public static FormDTO buildFromEntity(Form form) {
        return FormDTO.builder().id(form.getId()).formType(form.getFormType()).build();
    }

}

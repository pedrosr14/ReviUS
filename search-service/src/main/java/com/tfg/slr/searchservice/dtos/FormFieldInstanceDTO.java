package com.tfg.slr.searchservice.dtos;

import com.tfg.slr.searchservice.models.FormFieldInstance;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.Normalizer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormFieldInstanceDTO {

    private Long id;

    private Long formId;

    private String fieldName;

    private String fieldValue;

}

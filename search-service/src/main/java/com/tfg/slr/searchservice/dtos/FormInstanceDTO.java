package com.tfg.slr.searchservice.dtos;

import com.tfg.slr.searchservice.models.FormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
public class FormInstanceDTO {

    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FormType formType;

    private Long formId;
}

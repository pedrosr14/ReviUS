package com.tfg.review.dtos;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormCreationDTO {

    @NotBlank
    private String formType;

    @Valid
    private List<FormFieldDTO> fieldDTOS;
}

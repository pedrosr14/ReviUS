package com.tfg.slr.searchservice.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SelectionCriteriaDTO {

    private Long id;

    @NotBlank
    private String criterion;

    @NotNull
    private String criteriaType;

}
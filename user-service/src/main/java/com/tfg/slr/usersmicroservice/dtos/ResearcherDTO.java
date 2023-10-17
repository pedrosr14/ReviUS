package com.tfg.slr.usersmicroservice.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResearcherDTO {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String rol;
    private Long userId;
}

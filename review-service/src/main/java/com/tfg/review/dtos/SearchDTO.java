package com.tfg.review.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class SearchDTO {

    private Long id;

    private String searchReference;

    @NotBlank
    private String searchString;

    @NotBlank String observations;
    private Long dataSourceId;

    private Long protocolId;
}
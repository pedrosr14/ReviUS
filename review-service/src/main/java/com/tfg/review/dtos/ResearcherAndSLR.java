package com.tfg.review.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResearcherAndSLR {

    @Valid
    private UserDTO userDTO;

    @Valid
    private SLRDTO slrDTO;
}

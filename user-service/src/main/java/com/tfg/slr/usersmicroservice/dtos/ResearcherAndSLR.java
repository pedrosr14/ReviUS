package com.tfg.slr.usersmicroservice.dtos;

import lombok.*;

import javax.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ResearcherAndSLR {

    @Valid
    private UserDTO userDTO;

    @Valid
    private SLRDTO slrDTO;
}

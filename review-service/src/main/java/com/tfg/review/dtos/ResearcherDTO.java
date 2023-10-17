package com.tfg.review.dtos;

import com.tfg.review.models.Researcher;
import com.tfg.review.models.Rol;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResearcherDTO {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private Rol rol;
    private Long userId;

    public static ResearcherDTO buildFromEntity(Researcher researcher){
        return ResearcherDTO.builder().id(researcher.getId()).name(researcher.getName()).rol(researcher.getRol())
                .userId(researcher.getUserId()).build();
    }
}

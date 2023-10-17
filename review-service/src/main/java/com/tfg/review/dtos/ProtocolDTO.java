package com.tfg.review.dtos;

import com.tfg.review.models.Language;
import com.tfg.review.models.Protocol;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProtocolDTO {

    @NotBlank
    private String principalQuestion;

    private String secondaryQuestion;

    @NotNull
    private List<Language> languages = new ArrayList<>();

    public static Protocol buildEntity(ProtocolDTO dto){
        return Protocol.builder().principalQuestion(dto.getPrincipalQuestion())
                .secondaryQuestion(dto.getSecondaryQuestion()).languages(dto.getLanguages()).build();
    }

}

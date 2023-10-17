package com.tfg.review.dtos;

import com.tfg.review.models.Keyword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class KeywordDTO {

    private Long id;
    @NotBlank
    private String keyword;
    private List<Long> protocols = new ArrayList<>();

    public static KeywordDTO buildFromEntity(Keyword keyword){
        KeywordDTO dto = KeywordDTO.builder().id(keyword.getId()).keyword(keyword.getKeyword()).build();
        if(keyword.getProtocols()!=null&&!keyword.getProtocols().isEmpty()){
            List<Long> protocolIds = keyword.getProtocols().stream().map(protocol -> protocol.getId()).collect(Collectors.toList());
            dto.setProtocols(protocolIds);
        }
        return dto;
    }

    public static Keyword buildEntity(KeywordDTO dto) {
        Keyword result = Keyword.builder().id(dto.getId()).keyword(dto.getKeyword()).protocols(new HashSet<>()).build();
        return result;
    }

}

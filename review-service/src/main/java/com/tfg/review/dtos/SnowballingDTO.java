package com.tfg.review.dtos;

import com.tfg.review.models.Protocol;
import com.tfg.review.models.Snowballing;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SnowballingDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String source;
    @NotBlank
    private String snowballingType;
    private Long studyId;
    private Set<Long> protocols;

    public static SnowballingDTO  buildFromEntity(Snowballing snowballing){
        SnowballingDTO dto = SnowballingDTO.builder().id(snowballing.getId()).name(snowballing.getName())
                .source(snowballing.getSource()).snowballingType(snowballing.getSnowballingType().toString()).protocols(new HashSet<>()).build();
        if(snowballing.getProtocols()!=null && !snowballing.getProtocols().isEmpty()){
            for(Protocol protocol : snowballing.getProtocols()) {
                dto.protocols.add(protocol.getId());
            }
        }
        return dto;
    }

    public static Snowballing buildEntity(SnowballingDTO dto){
        return Snowballing.builder().source(dto.getSource()).studyId(dto.getStudyId()).build();
    }

    public static SnowballingDTO buildFromDataSource(DataSourceDTO dto){
        return SnowballingDTO.builder().name(dto.getName()).source(dto.getSource()).snowballingType(dto.getSnowballingType()).studyId(dto.getStudyId().longValue()).build();
    }
}

package com.tfg.review.dtos;

import com.tfg.review.models.DataSource;
import com.tfg.review.models.Protocol;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DataSourceDTO {

    private Long id;
    @NotBlank
    private String name;
    private String source;
    private String url;
    private String snowballingType;
    private Long studyId;
    private Set<Long> protocols;

    public static DataSourceDTO buildFromEntity(DataSource dataSource){
        DataSourceDTO dto = DataSourceDTO.builder().id(dataSource.getId()).name(dataSource.getName())
                .protocols(new HashSet<>()).build();
            if(dataSource.getProtocols()!=null && !dataSource.getProtocols().isEmpty()){
                for(Protocol protocol : dataSource.getProtocols()) {
                    dto.protocols.add(protocol.getId());
                }
            }
        return dto;
    }
}

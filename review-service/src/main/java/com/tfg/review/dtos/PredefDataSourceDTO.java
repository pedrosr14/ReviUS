package com.tfg.review.dtos;

import com.tfg.review.models.PredefDataSource;
import com.tfg.review.models.Protocol;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PredefDataSourceDTO {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @URL
    private String url;
    @NotBlank
    private Set<Long> protocols = new HashSet<>();

    public static PredefDataSourceDTO buildFromEntity(PredefDataSource dataSource){
        PredefDataSourceDTO dto = PredefDataSourceDTO.builder().id(dataSource.getId()).name(dataSource.getName())
                .url(dataSource.getUrl()).protocols(new HashSet<>()).build();
        if(dataSource.getProtocols()!=null && !dataSource.getProtocols().isEmpty()){
            for(Protocol protocol : dataSource.getProtocols()) {
                dto.protocols.add(protocol.getId());
            }
        }
        return dto;
    }

    public static PredefDataSource buildEntity(PredefDataSourceDTO dto){
        return PredefDataSource.builder().url(dto.url).build();
    }

    public static PredefDataSourceDTO buildFromDataSource(DataSourceDTO dto){
        return PredefDataSourceDTO.builder().name(dto.getName()).url(dto.getUrl()).build();
    }
}

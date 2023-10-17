package com.tfg.review.dtos;

import com.tfg.review.models.CustomDataSource;
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
public class CustomDataSourceDTO {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String source;
    @NotBlank
    private Set<Long> protocols = new HashSet<>();

    public static CustomDataSourceDTO buildFromEntity(CustomDataSource dataSource){
        CustomDataSourceDTO dto = CustomDataSourceDTO.builder().id(dataSource.getId()).name(dataSource.getName())
                .source(dataSource.getSource()).protocols(new HashSet<>()).build();
        if(dataSource.getProtocols()!=null && !dataSource.getProtocols().isEmpty()){
            for(Protocol protocol : dataSource.getProtocols()) {
                dto.protocols.add(protocol.getId());
            }
        }
        return dto;
    }

    public static CustomDataSource buildEntity(CustomDataSourceDTO dto){
       return CustomDataSource.builder().source(dto.getSource()).build();
    }

    public static CustomDataSourceDTO buildFromDataSource(DataSourceDTO dto){
        return CustomDataSourceDTO.builder().name(dto.getName()).source(dto.getSource()).build();
    }
}

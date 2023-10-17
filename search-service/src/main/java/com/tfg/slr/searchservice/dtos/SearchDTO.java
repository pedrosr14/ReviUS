package com.tfg.slr.searchservice.dtos;

import com.tfg.slr.searchservice.models.Search;
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

    public static Search buildEntity(SearchDTO searchDTO){
        return Search.builder().searchString(searchDTO.getSearchString())
                .observations(searchDTO.getObservations()).build();
    }

    public static SearchDTO buildFromEntity(Search search){
        return SearchDTO.builder().id(search.getId()).searchReference(search.getSearchReference()).searchString(search.getSearchString())
                .observations(search.getObservations()).dataSourceId(search.getDataSourceId()).build();
    }

}

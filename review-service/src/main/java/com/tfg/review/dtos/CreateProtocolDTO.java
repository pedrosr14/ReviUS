package com.tfg.review.dtos;

import lombok.*;

import javax.validation.Valid;
import java.util.HashSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateProtocolDTO {

    @Valid
    private ProtocolDTO protocolDTO;
    @Valid
    private HashSet<KeywordDTO> keywords;
    @Valid
    private HashSet<SelectionCriteriaDTO> criteria;
    @Valid
    private HashSet<DataSourceDTO> dataSources;

}

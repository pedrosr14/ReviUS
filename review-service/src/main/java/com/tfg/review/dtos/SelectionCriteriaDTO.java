package com.tfg.review.dtos;

import com.tfg.review.models.CriteriaType;
import com.tfg.review.models.SelectionCriteria;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class SelectionCriteriaDTO {

    private Long id;

    @NotBlank
    private String criterion;

    @NotNull
    private String criteriaType;

    private List<String> protocols = new ArrayList<>();

    public static SelectionCriteriaDTO buildFromEntity(SelectionCriteria criteria){
        SelectionCriteriaDTO result = SelectionCriteriaDTO.builder().id(criteria.getId()).criterion(criteria.getCriterion())
                .criteriaType(criteria.getCriteriaType().toString()).build();
        if(criteria.getProtocols()!=null && !criteria.getProtocols().isEmpty()){
            List<String> protocolsList = criteria.getProtocols().stream().map(protocol -> protocol.getId()+": "+protocol.getPrincipalQuestion()).collect(Collectors.toList());
            result.setProtocols(protocolsList);
        }
        return result;
    }

    public static SelectionCriteria buildEntity(SelectionCriteriaDTO dto){
        SelectionCriteria result = SelectionCriteria.builder().criterion(dto.getCriterion()).protocols(new HashSet<>()).build();
        if(dto.getCriteriaType().equals("INCLUSION")) {
            result.setCriteriaType(CriteriaType.INCLUSION);
        }else if(dto.getCriteriaType().equals("EXCLUSION")){
            result.setCriteriaType(CriteriaType.EXCLUSION);
        }else{
            result.setCriteriaType(null);
        }
        return result;
    }
}

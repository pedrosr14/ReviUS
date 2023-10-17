package com.tfg.review.dtos;

import com.tfg.review.models.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FullProtocolDTO {

    private Long id;

    @NotBlank
    private String principalQuestion;
    private String secondaryQuestion;
    @NotNull
    private List<Language> languages = new ArrayList<>();

    private Long slrId;
    private Set<String> dataSources = new HashSet<>();
    private Set<String> keywords = new HashSet<>();
    private Set<String> selectionCriteria = new HashSet<>();
    private Long extractionFormId;
    private Long qualityFormId;

    public static FullProtocolDTO buildFromEntity (Protocol protocol) {
        FullProtocolDTO dto = FullProtocolDTO.builder().id(protocol.getId())
                .principalQuestion(protocol.getPrincipalQuestion()).secondaryQuestion(protocol.getSecondaryQuestion())
                .languages(protocol.getLanguages()).dataSources(new HashSet<>()).keywords(new HashSet<>()).selectionCriteria(new HashSet<>()).build();

        if(protocol.getSlr() != null) dto.setSlrId(protocol.getSlr().getId());
        if(protocol.getExtractionForm() != null) dto.setExtractionFormId(protocol.getExtractionForm().getId());
        if(protocol.getQualityForm() != null) dto.setQualityFormId(protocol.getQualityForm().getId());
        if(protocol.getKeywords()!= null && !protocol.getKeywords().isEmpty()) {
            for(Keyword keyword: protocol.getKeywords()){
                dto.keywords.add("id: "+keyword.getId()+" - "+keyword.getKeyword());
            }
        }
        if(protocol.getDataSources()!= null && !protocol.getDataSources().isEmpty()) {
            for(DataSource dataSource: protocol.getDataSources()){
                dto.dataSources.add("id: "+dataSource.getId()+" - "+dataSource.getName());
            }
        }
        if(protocol.getSelectionCriteria()!= null && !protocol.getSelectionCriteria().isEmpty()) {
            for(SelectionCriteria selectionCriteria: protocol.getSelectionCriteria()){
                dto.selectionCriteria.add("id: "+selectionCriteria.getId()+" - "+selectionCriteria.getCriterion());
            }
        }
        return dto;
    }
}

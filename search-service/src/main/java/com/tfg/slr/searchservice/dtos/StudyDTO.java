package com.tfg.slr.searchservice.dtos;

import com.tfg.slr.searchservice.models.Status;
import com.tfg.slr.searchservice.models.Study;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class StudyDTO {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private Integer year;

    @NotBlank
    private String type;

    private String venue;

    @NotBlank
    @URL
    private String sourceURL;

    private String priority;

    private String selectionStatus;

    private String extractionStatus;

    private Integer score;

    @NotBlank
    private String DOI;

    private String search;
    private String dataSource;

    public static Study buildEntity(StudyDTO dto){
        return Study.builder().title(dto.getTitle()).author(dto.getAuthor()).year(dto.getYear())
                .venue(dto.getVenue()).type(dto.getType()).sourceURL(dto.getSourceURL()).priority(dto.getPriority())
                .score(dto.getScore()).DOI(dto.getDOI()).build();
    }

    public static StudyDTO buildFromEntity(Study study){
        StudyDTO dto = StudyDTO.builder().id(study.getId()).title(study.getTitle()).author(study.getAuthor()).year(study.getYear())
                .venue(study.getVenue()).type(study.getType()).sourceURL(study.getSourceURL()).priority(study.getPriority())
                .selectionStatus(study.getSelectionStatus().toString()).extractionStatus(study.getExtractionStatus().toString())
                .score(study.getScore()).DOI(study.getDOI()).build();
        if(study.getSearch()!=null){
            dto.setSearch(study.getSearch().getSearchReference());
        }
        return dto;
    }
}

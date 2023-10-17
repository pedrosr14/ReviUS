package com.tfg.review.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.SLR;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FullSLRDTO {

    private Long id;

    @NotBlank(message="title can not be blank")
    private String title;

    @NotBlank(message="description can not be blank")
    private String description;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past
    private Date initDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @NotBlank
    private String workField;

    @NotNull
    private String objective;

    @NotNull
    private Boolean publicVisibility;

    private Long protocolId = null;
    private Long reportId = null;
    private List<String> researchers = new ArrayList<>();

    public static FullSLRDTO buildFromEntity (SLR slr) {
        FullSLRDTO dto = FullSLRDTO.builder().id(slr.getId()).title(slr.getTitle()).description(slr.getDescription())
                .initDate(slr.getInitDate()).endDate(slr.getEndDate()).workField(slr.getWorkField())
                .objective(slr.getObjective()).publicVisibility(slr.getPublicVisibility()).researchers(new ArrayList<>()).build();
        if(slr.getProtocol() != null) dto.setProtocolId(slr.getProtocol().getId());
        if(slr.getReport() != null) dto.setReportId(slr.getReport().getId());
        if(slr.getResearchers()!= null && !slr.getResearchers().isEmpty()) {
            for(Researcher researcher: slr.getResearchers()){
                dto.researchers.add(researcher.getName());
            }
        }
        return dto;
    }
}

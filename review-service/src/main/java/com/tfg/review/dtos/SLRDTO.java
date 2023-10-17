package com.tfg.review.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.SLR;
import lombok.*;
import org.hibernate.mapping.Collection;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SLRDTO {

    private Long id;

    @NotBlank(message="title can not be blank")
    private String title;

    @NotBlank(message="description can not be blank")
    private String description;

    @NotBlank
    private String workField;

    @NotBlank
    private String objective;

    @NotNull
    private Boolean publicVisibility;

    private List<String> researchers = new ArrayList<>();

    public static SLRDTO buildFromEntity (SLR slr) {
        return SLRDTO.builder().id(slr.getId()).title(slr.getTitle()).description(slr.getDescription())
                .workField(slr.getWorkField()).objective(slr.getObjective())
                .researchers(slr.getResearchers().stream().map(Researcher::getName).collect(Collectors.toList())).publicVisibility(slr.getPublicVisibility()).build();
    }
    public static SLR buildEntity(SLRDTO dto){
        return SLR.builder().title(dto.getTitle()).description(dto.getDescription()).workField(dto.getWorkField())
                .objective(dto.getObjective()).publicVisibility(dto.getPublicVisibility()).build();
    }

}

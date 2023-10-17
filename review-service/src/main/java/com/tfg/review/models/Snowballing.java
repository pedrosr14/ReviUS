package com.tfg.review.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "snowballing")
public class Snowballing extends DataSource{

    //--Attributes--//
    @NotBlank
    @Column(name="source")
    private String source;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SnowballingType snowballingType;

    private Long studyId;
}

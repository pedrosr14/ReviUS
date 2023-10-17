package com.tfg.review.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "report")
public class Report {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="report_id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name="resume", columnDefinition = "TEXT")
    private String resume;

    @NotBlank
    @Column(name="conclusions", columnDefinition = "TEXT")
    private String conclusions;

    @NotBlank
    @Column(name="analysis", columnDefinition = "TEXT")
    private String analysis;

    //--Relationships--//
    @Valid
    @NotNull
    @OneToOne(mappedBy = "report", fetch = FetchType.EAGER, orphanRemoval = true, optional = false) //orphanRemoval set to true to match composition in UML, this deletes a Report if it's detached from its SLR
    private SLR slr;
}

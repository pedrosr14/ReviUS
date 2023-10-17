package com.tfg.slr.searchservice.models;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name="study")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="study_id")
    private Long id;

    @NotBlank
    @Column(name="title", columnDefinition = "TEXT")
    private String title;

    @NotBlank
    @Column(name="author")
    private String author;

    @NotNull
    @Column(name="year")
    private Integer year;

    @NotBlank
    @Column(name="type")
    private String type;

    @Column(name="venue")
    private String venue;

    @NotBlank
    @URL
    @Column(name="source_url")
    private String sourceURL;

    @Column(name="priority")
    private String priority;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private Status selectionStatus;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private Status extractionStatus;

    @NotNull
    @Column(name="score")
    private Integer score;

    @NotBlank
    @Column(name="doi")
    private String DOI;

    @ElementCollection
    private Set<Long> appliedCriteriaIds = new HashSet<>();

    //--Relationships--//

    @Valid
    @ManyToOne
    @JoinColumn(name="search_id", nullable = false)
    private Search search;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "extraction_form_instance_id")
    private FormInstance extractionFormInstance;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quality_form_instance_id")
    private FormInstance qualityFormInstance;
}

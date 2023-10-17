package com.tfg.review.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "slr")
public class SLR {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="slr_id", nullable = false)
    private Long id;

    @NotBlank(message="title can not be blank")
    @Column(name="title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @NotBlank(message="description can not be blank")
    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past
    @Column(name = "init_date")
    private Date initDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="end_date")
    private Date endDate;

    @NotBlank
    @Column(name="work_field")
    private String workField;

    @NotBlank
    @Column(name="objective", columnDefinition = "TEXT")
    private String objective;

    @NotNull
    @Column(name="public_visibility")
    private Boolean publicVisibility;

    //--Relationships--//
    @Valid //Means that the attached entity in the relationship should have all its fields validated
    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER) // composition mapped via cascade & we want to load it together
    @JoinColumn(name="protocol_id") //foreign key
    //@PrimaryKeyJoinColumn //alternative
    private Protocol protocol;

    @Valid
    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER) //composition & we want to load them together
    @JoinColumn(name="report_id")
    private Report report;

    @Valid
    @ManyToMany(mappedBy = "SLRs")
    @ToString.Exclude
    private Set<Researcher> researchers = new HashSet<>();

    public void addResearcher(Researcher researcher){
        researchers.add(researcher);
        researcher.getSLRs().add(this);
    }
    public void removeResearcher(Researcher researcher){
        researchers.remove(researcher);
        researcher.getSLRs().remove(this);
    }

}

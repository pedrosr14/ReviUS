package com.tfg.review.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name="researcher")
public class Researcher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="researcher_id")
    private Long id;

    @NotBlank
    @Column(name="name")
    private String name;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Long userId;

    //--Relationships--//
    @Valid
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name="researcher_slr",
            joinColumns = @JoinColumn(name="researcher_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name="slr_id",nullable = false))
    private Set<SLR> SLRs = new HashSet<>();
    public void addSLR(SLR slr){
        SLRs.add(slr);
        slr.getResearchers().add(this);
    }
    public void removeSLR(SLR slr){
        SLRs.remove(slr);
        slr.getResearchers().remove(this);
    }
}

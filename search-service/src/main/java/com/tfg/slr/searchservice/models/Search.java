package com.tfg.slr.searchservice.models;

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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name="search")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="search_id")
    private Long id;

    @NotBlank
    @Column(unique = true, name = "search_ref")
    private String searchReference;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past
    @Column(name = "search_date")
    private Date searchDate;

    @NotBlank
    @Column(name="search_string")
    private String searchString;

    @NotBlank
    @Column(name="observations", columnDefinition = "TEXT")
    private String observations;

    private Long dataSourceId;
    private Long protocolId;

    //--Relationships---//

    @Valid
    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Study> studies = new HashSet<>();

}

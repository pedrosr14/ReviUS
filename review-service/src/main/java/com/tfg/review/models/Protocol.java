package com.tfg.review.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "protocol")
public class Protocol {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="protocol_id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name="principal_question", columnDefinition = "TEXT")
    private String principalQuestion;

    @Column(name="secondary_question", columnDefinition = "TEXT")
    private String secondaryQuestion;

    @NotNull
    @ElementCollection //embedded collection attribute
    @CollectionTable(
            name="protocol_languages", //separated table for the relationship
            joinColumns=@JoinColumn(name="protocol_id",nullable = false) //foreign key of the table referencing the father key
    )
    private List<Language> languages = new ArrayList<>();

    //--Relationships--//
    @Valid
    @NotNull
    @OneToOne (mappedBy = "protocol", orphanRemoval = true) //mappedBy because it's bidirectional, with protocol being the attribute on owner side
    private SLR slr;                   //this creates the column slr_slr_id, because slr is the attribute and slr_id is the primary key of the owner side

    //Can be null, so we can create and save a protocol before adding data sources
    @Valid
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE}) //in this case may be better the lazy load
    @JoinTable(name="protocol_data_sources",
    joinColumns = @JoinColumn(name="protocol_id", nullable = false), //the relationship can be null but once the relationship is established, both keys must be present
    inverseJoinColumns = @JoinColumn(name="data_source_id", nullable = false))
    private Set<DataSource> dataSources = new HashSet<>();

    public void addDataSource(DataSource dataSource){
        dataSources.add(dataSource);
        dataSource.getProtocols().add(this);
    }

    public void removeDataSource(DataSource dataSource){
        dataSources.remove(dataSource);
        dataSource.getProtocols().remove(this);
    }

    @Valid
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE}) //We don't want to propagate REMOVE operations, but persist and merge make sense
    @Fetch(value = FetchMode.SUBSELECT) //Hibernate does not permit more than one eager load, this solves it. Loads the set in a sub-select query
    @JoinTable(name="protocol_keywords",
    joinColumns = @JoinColumn(name="protocol_id", nullable = false),
    inverseJoinColumns = @JoinColumn(name="keyword_id", nullable = false))
    private Set<Keyword> keywords = new HashSet<>();

    public void addKeyword(Keyword keyword){
        keywords.add(keyword);
        keyword.getProtocols().add(this);
    }

    public void removeKeyword(Keyword keyword){
        keywords.remove(keyword);
        keyword.getProtocols().remove(this);
    }

    @Valid
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE}) //in this case I think is better to load them together as the others relationships of Protocol. There should not be an enormous amount of items
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name="protocol_selection_criteria",
    joinColumns = @JoinColumn(name="protocol_id", nullable = false),
    inverseJoinColumns = @JoinColumn(name="selection_criteria_id",nullable = false))
    private Set<SelectionCriteria> selectionCriteria = new HashSet<>();

    public void addSelectionCriteria(SelectionCriteria criteria){
        selectionCriteria.add(criteria);
        criteria.getProtocols().add(this);
    }

    public void removeSelectionCriteria(SelectionCriteria criteria){
        selectionCriteria.remove(criteria);
        criteria.getProtocols().remove(this);
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="extraction_form_id")
    private Form extractionForm;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="quality_form_id")
    private Form qualityForm;

    //--Other methods--//

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Protocol))
            return false;

        Protocol protocol = (Protocol) o;
        return id != null && id.equals(protocol.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

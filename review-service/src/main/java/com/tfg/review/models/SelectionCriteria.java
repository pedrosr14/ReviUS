package com.tfg.review.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name="selection_criteria")
public class SelectionCriteria {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "selection_criteria_id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name="criterion", columnDefinition = "TEXT")
    private String criterion;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private CriteriaType criteriaType;

    //--Relationships--/

    @Valid
    @ManyToMany(mappedBy = "selectionCriteria")
    @ToString.Exclude
    private Set<Protocol> protocols = new HashSet<>();

    //--Other methods--//
    public void addProtocol(Protocol protocol){
        this.protocols.add(protocol);
        protocol.getSelectionCriteria().add(this);

    }

    public void removeProtocol(Protocol protocol){
        this.protocols.remove(protocol);
        protocol.getSelectionCriteria().remove(this);
    }


    //It's necessary to override equals and hashcode because we use Set as collection in the relationship
    @Override
    public boolean equals(Object o){
        if(this == o) return true;

        if(!(o instanceof SelectionCriteria)) return false;

        SelectionCriteria that = (SelectionCriteria) o;

        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}

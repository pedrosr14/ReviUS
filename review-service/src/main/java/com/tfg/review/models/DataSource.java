package com.tfg.review.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "data_source")
@Inheritance(strategy = InheritanceType.JOINED) //this maps a table per class, with a bigger class for DataSource
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="data_source_id")
    private Long id;

    @NotBlank
    @Column(name="name")
    private String name;

    //--Relationships--//
    @Valid
    @ManyToMany(mappedBy="dataSources", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Protocol> protocols = new HashSet<>();

    //--Other methods--//

    public void addProtocol (Protocol protocol){
        this.protocols.add(protocol);
        protocol.getDataSources().add(this);
    }

    public void removeProtocol (Protocol protocol){
        this.protocols.remove(protocol);
        protocol.getDataSources().remove(this);
    }
    @Override
    public boolean equals(Object o){

        if (this == o) return true;
        if (!(o instanceof DataSource)) return false;
        DataSource dataSource = (DataSource) o;
        return this.id != null && id.equals(dataSource.getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}

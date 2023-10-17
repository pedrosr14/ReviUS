package com.tfg.review.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "keyword")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name="keyword")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "keyword_id", nullable = false)
    private Long id;

    @NotBlank(message = "Keyword can not be null nor blank")
    @Column(name="keyword", nullable = false, unique = true)
    private String keyword;

    //--Relationships--//
    @Valid
    @ManyToMany(mappedBy = "keywords")
    @ToString.Exclude
    private Set<Protocol> protocols = new HashSet<>();

    public void addProtocol (Protocol protocol){
        this.protocols.add(protocol);
        protocol.getKeywords().add(this);
    }

    public void removeProtocol (Protocol protocol){
        this.protocols.remove(protocol);
        protocol.getKeywords().remove(this);
    }

    //--Other methods--//

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (!(o instanceof Keyword)) return false;

        Keyword keyword = (Keyword) o;

        return this.getKeyword().equals(keyword.getKeyword());
    }

   @Override
   public int hashCode(){

        return Objects.hash(this.getKeyword());
   }


}

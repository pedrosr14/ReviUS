package com.tfg.review.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name="form_field")
public class FormField {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "form_field_id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name = "field_name", columnDefinition = "TEXT")
    private String fieldName;

    @NotBlank
    @Column(name="field_text")
    private String fieldType;

    //--Relationships--//

    @Valid
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="form_id", nullable = false) //foreign key. Is better to have it on the many side, so all have the id of the one side referenced
    private Form form;
}

package com.tfg.slr.searchservice.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="form_field_instance")
public class FormFieldInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="form_field_instance_id")
    private Long id;

    @NotBlank
    @Column(name="name")
    private String name;

    @NotBlank
    @Column(name="value", columnDefinition = "TEXT")
    private String value;

    //--Relationships--//

    @Valid
    @ManyToOne
    @JoinColumn(name="form_instance_id", nullable = false)
    @ToString.Exclude
    private FormInstance formInstance;
}

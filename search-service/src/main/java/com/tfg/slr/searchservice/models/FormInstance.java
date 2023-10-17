package com.tfg.slr.searchservice.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="form_instance")
public class FormInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="form_instance_id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="form_type")
    private FormType formType;

    private Long formId;

    //--Relationships--//

    @Valid
    @OneToOne(mappedBy = "extractionFormInstance")
    private Study studyToExtract;

    @Valid
    @OneToOne(mappedBy = "qualityFormInstance")
    private Study studyToQuality;

    @Valid
    @OneToMany(mappedBy = "formInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<FormFieldInstance> fields = new HashSet<>();


}

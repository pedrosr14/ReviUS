package com.tfg.review.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name="form")
public class Form {

    //--Attributes--//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "form_id", nullable = false)
    private Long id;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private FormType formType;

    //--Relationships--//

    @NotNull
    @Valid
    @OneToOne(mappedBy = "extractionForm")
    private Protocol protocolToExtraction;

    @NotNull
    @Valid
    @OneToOne(mappedBy = "qualityForm")
    private Protocol protocolToQuality;

    @Valid
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormField> formFields = new ArrayList<>();
}

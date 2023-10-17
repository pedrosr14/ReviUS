package com.tfg.review.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "custom_data_source")
public class CustomDataSource extends DataSource {

    //--Attributes--//
    @NotBlank
    @Column(name="source")
    private String source;
}

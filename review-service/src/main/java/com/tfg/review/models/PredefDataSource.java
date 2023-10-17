package com.tfg.review.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

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
@Table(name = "predef_data_source")
public class PredefDataSource extends DataSource{

    //--Attributes--//
    @NotBlank
    @URL
    @Column(name="url")
    private String url;
}

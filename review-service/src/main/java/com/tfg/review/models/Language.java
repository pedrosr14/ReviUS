package com.tfg.review.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.persistence.Embeddable;

//It's Embeddable into another Entity, so it's not defined as Entity
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Language {

    @NotBlank
    private String language;
}

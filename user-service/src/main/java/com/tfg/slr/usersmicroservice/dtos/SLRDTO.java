package com.tfg.slr.usersmicroservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class SLRDTO {

    private Long id;
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String workField;

    @NotBlank
    private String objective;

    @NotNull
    private Boolean publicVisibility;
}

package com.tfg.slr.usersmicroservice.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NewPasswordDTO {

    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 8, max = 32) //Password must be minimum 8 characters and maximum 32
    @Pattern(regexp = "^(?=.*[ña-z])(?=.*[ÑA-Z])(?=.*\\d)[ña-zÑA-Z\\d]{8,32}$")
    private String oldPassword;
    @NotBlank
    @Size(min = 8, max = 32) //Password must be minimum 8 characters and maximum 32
    @Pattern(regexp = "^(?=.*[ña-z])(?=.*[ÑA-Z])(?=.*\\d)[ña-zÑA-Z\\d]{8,32}$")
    private String newPassword;

}

package com.tfg.slr.usersmicroservice.dtos;

import com.tfg.slr.usersmicroservice.models.UserAccount;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class UserAccountDTO {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8, max = 32) //Password must be minimum 8 characters and maximum 32
    @Pattern(regexp = "^(?=.*[ña-z])(?=.*[ÑA-Z])(?=.*\\d)[ña-zÑA-Z\\d]{8,32}$")
    private String password;

    public static UserAccountDTO buildFromEntity(UserAccount userAccount) {
        UserAccountDTO dto = new UserAccountDTO();

        dto.setUsername(userAccount.getUserName());
        dto.setPassword(userAccount.getPassword());

        return dto;
    }

    public static UserAccount createEntity(UserAccountDTO dto){
        UserAccount account = UserAccount.builder()
                    .userName(dto.getUsername()).password(dto.getPassword()).isAdmin(false).build();

        return account;
    }
}

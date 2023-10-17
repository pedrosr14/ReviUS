package com.tfg.slr.usersmicroservice.dtos;

import com.tfg.slr.usersmicroservice.models.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class UserDTO {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String workField;
    @NotBlank
    private String institution;

    public static UserDTO fromEntity(User user){
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getCompleteName());
        dto.setInstitution(user.getInstitution());
        dto.setWorkField(user.getWorkField());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public static User createEntity(UserDTO dto){

       return User.builder().completeName(dto.name).institution(dto.getInstitution()).workField(dto.getWorkField()).email(dto.getEmail()).build();
    }

}

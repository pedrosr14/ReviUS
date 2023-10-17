package com.tfg.slr.usersmicroservice.dtos;

import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserProfileDTO {

    private String completeName;
    private String email;
    private String workField;
    private String institution;
    private Long userAccount_id;
    private String username;

    public UserProfileDTO (User user, UserAccount userAccount){
        this.completeName = user.getCompleteName();
        this.email = user.getEmail();
        this.institution = user.getInstitution();
        this.workField = user.getWorkField();
        this.userAccount_id = userAccount.getId();
        this.username = userAccount.getUserName();
    }
}

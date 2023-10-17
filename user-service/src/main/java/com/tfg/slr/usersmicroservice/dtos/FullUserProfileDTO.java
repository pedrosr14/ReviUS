package com.tfg.slr.usersmicroservice.dtos;

import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FullUserProfileDTO {

        private Long userId;
        private String username;
        private String completeName;
        private String email;
        private String workField;
        private String institution;
        private List<String> slrs;

        public static FullUserProfileDTO buildProfile (User user, UserAccount userAccount){
            return FullUserProfileDTO.builder().userId(user.getId()).username(userAccount.getUserName())
                    .completeName(user.getCompleteName()).email(user.getEmail()).workField(user.getWorkField())
                    .institution(user.getInstitution()).slrs(new ArrayList<>()).build();
        }

}

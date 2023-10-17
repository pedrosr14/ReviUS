package com.tfg.slr.usersmicroservice.services;

import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {

    Optional<UserAccount> findByUserName(String username);

    boolean exists (Long userAccountId);

    UserAccount findOne(Long id);

    List<UserAccount> findAll();

    /**
     *Create new user account
     */
    UserAccount registerUserAccount(UserAccountDTO accountDto);

    UserAccount update(UserAccount userAccount);
    void delete(Long userAccountId);

    //--Assistant methods--//

    /**
     *
     * @param userAccount of the user we want to authenticate
     * @return Authentication object with the authenticated user data
     */
    Authentication authenticate(UserAccountDTO userAccount);

    void changePassword(Long userAccountId, String oldPassword, String newPassword);

    void changeAuthority(Long userAccountId, boolean admin);

    List<GrantedAuthority> getAuthorities(UserAccount account);
}

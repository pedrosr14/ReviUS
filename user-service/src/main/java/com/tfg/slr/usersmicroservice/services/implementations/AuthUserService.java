package com.tfg.slr.usersmicroservice.services.implementations;

import com.tfg.slr.usersmicroservice.dtos.TokenDTO;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.models.AuthUser;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This class implements Spring class UserDetailsService, like AuthUser implements UserDetails
 * Allows to transform a UserAccount to a AuthUser
 */
@Transactional
@Service
@AllArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final UserAccountService userAccountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount userAccount = userAccountService.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException(MessageConstants.USERNAME_NOT_FOUND));
        return AuthUser.buildFromUserAccount(userAccount);
    }

    public UserAccount getAuthUserAccount() {
        UserAccount authUserAccount = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) throw new IllegalArgumentException(MessageConstants.NO_USER_AUTHENTICATED);

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUser){
            AuthUser authUser = (AuthUser) principal;
            authUserAccount = userAccountService.findByUserName(authUser.getUsername()).orElseThrow(()->
                    new UserAccountNotFoundException(MessageConstants.PRINCIPAL_NOT_FOUND));
        }
        if(userAccountService.exists(authUserAccount.getId())){
            return authUserAccount;
        }else {
            throw new UserAccountNotFoundException(MessageConstants.PRINCIPAL_NOT_FOUND);
        }
    }

    public boolean validatePassword(String password) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalArgumentException(MessageConstants.NO_USER_AUTHENTICATED);
        Object principal = auth.getPrincipal();

        if (principal instanceof AuthUser) {
            AuthUser authUser = (AuthUser) principal;
            if (passwordEncoder.matches(password, authUser.getPassword())) {
                return true;
            }
        } else {
            throw new UserAccountNotFoundException(MessageConstants.PRINCIPAL_NOT_FOUND);
        }
        return false;
    }

    public TokenDTO validate(String token) {
        if(!jwtProvider.validateToken(token)){
            return null;
        }
        String username = jwtProvider.getUsernameFromToken(token);
        Optional<UserAccount> optionalAccount = userAccountService.findByUserName(username);
        if(optionalAccount.isEmpty()){
            return null;
        }
        return new TokenDTO(token);
    }
}

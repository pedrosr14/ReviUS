package com.tfg.slr.usersmicroservice.services.implementations;

import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.exceptions.IncorrectPasswordException;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.models.AuthUser;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.repositories.UserRepository;
import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import com.tfg.slr.usersmicroservice.utils.UserServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tfg.slr.usersmicroservice.repositories.UserAccountRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Optional<UserAccount> findByUserName(String username) {
        if (username == null) throw new IllegalArgumentException(MessageConstants.NULL_USERNAME);

        return userAccountRepository.findByUserName(username);
    }

    public boolean exists (Long userAccountId) {
        if (userAccountId == null) throw new IllegalArgumentException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND);

        return userAccountRepository.existsById(userAccountId);
    }

    public UserAccount findOne(Long id) {

        return userAccountRepository.findUserAccountById(id).orElseThrow(() -> new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));
    }

    public List<UserAccount> findAll() {

        return userAccountRepository.findAll();
    }

    public UserAccount registerUserAccount(UserAccountDTO accountDto) {
        if (accountDto == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);
        if (userAccountRepository.existsByUserName(accountDto.getUsername())) throw new IllegalArgumentException(MessageConstants.EXISTING_USER);

        UserAccount account = UserAccount.builder().userName(accountDto.getUsername())
                .password(accountDto.getPassword()).build();

        String plainPassword = account.getPassword();

        if(!UserServiceUtils.checkPasswordConstraints(plainPassword)){
            throw new IllegalArgumentException(MessageConstants.PASSWORD_HAS_ERRORS);
        }

        String hashedPassword = passwordEncoder.encode(plainPassword);

        account.setPassword(hashedPassword);
        account.setIsAdmin(false);
        return userAccountRepository.save(account);
    }

    public UserAccount update(UserAccount userAccount) {
        if (userAccount == null || !userAccountRepository.existsByUserName(userAccount.getUserName()))
            throw new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND);

        return userAccountRepository.save(userAccount);
    }

    public void delete(Long userAccountId) {
        if (userAccountId == null) throw new IllegalArgumentException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND);

        UserAccount userAccount = userAccountRepository.findUserAccountById(userAccountId).orElseThrow(() -> new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));
        if (userAccount.getUser() != null) {
            throw new IllegalArgumentException(MessageConstants.DELETE_PROFILE_FIRST);
        }

        userAccountRepository.delete(userAccount);
    }

    //--Other methods--//

    public Authentication authenticate(UserAccountDTO userAccount) {
        if (userAccount == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        UserAccount registeredAccount = userAccountRepository.findByUserName(userAccount.getUsername()).orElseThrow(() -> new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));

        if (passwordEncoder.matches(userAccount.getPassword(), registeredAccount.getPassword())) {
                //if username and password are correct, create an AuthUser with roles associated
                AuthUser authUser = AuthUser.buildFromUserAccount(registeredAccount);
                //With the auth user we can create the authentication object using the Spring Security class UsernamePasswordAuthenticationToken
                Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, getAuthorities(registeredAccount));
                return authentication;
        } else {
            throw new IncorrectPasswordException(MessageConstants.WRONG_PASSWORD);
        }
    }

    public void changePassword(Long userAccountId, String oldPassword, String newPassword) {
        if (userAccountId == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        if(!UserServiceUtils.checkPasswordConstraints(newPassword)){
            throw new IllegalArgumentException(MessageConstants.PASSWORD_HAS_ERRORS);
        }

        UserAccount userAccount = userAccountRepository.findUserAccountById(userAccountId).orElseThrow(() -> new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, userAccount.getPassword())) {
            throw new IllegalArgumentException(MessageConstants.WRONG_PASSWORD);
        }

        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(userAccount);
    }

    public void changeAuthority(Long userAccountId, boolean admin) {
        if (userAccountId == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        UserAccount userAccount = userAccountRepository.findUserAccountById(userAccountId).orElseThrow(() -> new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));

        userAccount.setIsAdmin(admin);
    }

    public List<GrantedAuthority> getAuthorities(UserAccount account){
        if(account == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if(account.getIsAdmin()){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}
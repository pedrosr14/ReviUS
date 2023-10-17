package junit;

import com.tfg.slr.usersmicroservice.dtos.TokenDTO;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.models.AuthUser;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {

    @InjectMocks
    private AuthUserService authUserService;

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    public void loadUserByUsername_OK(){
        UserAccount expected = UserAccount.builder().userName("username").password("Password1").isAdmin(false).build();

        when(userAccountService.findByUserName("username")).thenReturn(Optional.of(expected));

        UserDetails authUser = authUserService.loadUserByUsername("username");
        Assertions.assertTrue(authUser.getAuthorities().size() == 1);
    }

    @Test
    public void loadUserByUsername_KO(){
        Assertions.assertThrows(UsernameNotFoundException.class, ()-> authUserService.loadUserByUsername(null));
    }

    @Test
    public void loadUserByUsername_UsernameNotFound(){
        String username = "notAnUser";
        when(userAccountService.findByUserName(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, ()-> authUserService.loadUserByUsername(username));
    }

    @Test
    public void getAuthUserAccount_OK(){

        UserAccount principalUserAccount = UserAccount.builder().id(2L).userName("principal").password("Password1").isAdmin(true).build();
        AuthUser authUser = AuthUser.buildFromUserAccount(principalUserAccount);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER")));

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        when(userAccountService.findByUserName(authUser.getUsername())).thenReturn(Optional.of(principalUserAccount));
        when(userAccountService.exists(principalUserAccount.getId())).thenReturn(true);

        Assertions.assertEquals(principalUserAccount, authUserService.getAuthUserAccount());
    }

    @Test
    public void getAuthUserAccount_Null(){
       when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

       Assertions.assertThrows(IllegalArgumentException.class, ()-> authUserService.getAuthUserAccount());
    }

    @Test
    public void getAuthUserAccount_AccountNotFound(){
        UserAccount principalUserAccount = UserAccount.builder().id(2L).userName("principal").password("Password1").isAdmin(true).build();
        AuthUser authUser = AuthUser.buildFromUserAccount(principalUserAccount);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER")));
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        when(userAccountService.findByUserName(authUser.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserAccountNotFoundException.class, ()-> authUserService.getAuthUserAccount());
    }

    @Test
    public void getAuthUserAccount_IdDontExist(){
        UserAccount principalUserAccount = UserAccount.builder().id(2L).userName("principal").password("Password1").isAdmin(true).build();
        AuthUser authUser = AuthUser.buildFromUserAccount(principalUserAccount);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER")));

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        when(userAccountService.findByUserName(authUser.getUsername())).thenReturn(Optional.of(principalUserAccount));
        when(userAccountService.exists(principalUserAccount.getId())).thenReturn(false);

        Assertions.assertThrows(UserAccountNotFoundException.class, ()-> authUserService.getAuthUserAccount());
    }

    @Test
    public void validatePassword_OK(){
        String password = "PrincipalPassword1";

        UserAccount principalUserAccount = UserAccount.builder().id(2L).userName("principal").password(password).isAdmin(true).build();
        AuthUser authUser = AuthUser.buildFromUserAccount(principalUserAccount);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER")));

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        when(passwordEncoder.matches(password, authUser.getPassword())).thenReturn(true);

        Assertions.assertDoesNotThrow(()->authUserService.validatePassword(password));
    }

    @Test
    public void validatePassword_KO(){
        String password = "PrincipalPassword1";

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, ()->authUserService.validatePassword(password));
    }

    @Test
    public void validatePassword_NotFound(){
        String password = "PrincipalPassword1";

        UserAccount principalUserAccount = UserAccount.builder().id(2L).userName("principal").password(password).isAdmin(true).build();

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(null, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER")));

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);

        Assertions.assertThrows(UserAccountNotFoundException.class, ()->authUserService.validatePassword(password));
    }

    @Test
    public void validate_OK(){
        String token = "validToken";
        String username = "username";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn(username);
        when(userAccountService.findByUserName(username)).thenReturn(Optional.of(new UserAccount()));

        TokenDTO result = authUserService.validate(token);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(token, result.getToken());
    }

    @Test
    public void validate_KO(){
        String token = "invalidToken";

        when(jwtProvider.validateToken(token)).thenReturn(false);

        TokenDTO result = authUserService.validate(token);

        Assertions.assertNull(result);
    }

}

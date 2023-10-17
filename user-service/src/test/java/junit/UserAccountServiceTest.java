package junit;

import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.exceptions.IncorrectPasswordException;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.repositories.UserAccountRepository;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.implementations.UserAccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void findByUserName_OK(){

        when(userAccountRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(new UserAccount()));
        Assertions.assertDoesNotThrow(()-> userAccountService.findByUserName("username"));
    }

    @Test
    public void findByUserName_KO(){

        Assertions.assertThrows(IllegalArgumentException.class, ()-> userAccountService.findByUserName(null));
    }

    @Test
    public void exists_OK(){
        when(userAccountRepository.existsById(1L)).thenReturn(true);
        Assertions.assertTrue(userAccountService.exists(1L));
    }

    @Test
    public void exists_KO(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->userAccountService.exists(null));
    }

    @Test
    public void findOne_OK(){
        when(userAccountRepository.findUserAccountById(Mockito.anyLong())).thenReturn(Optional.of(new UserAccount()));

        UserAccount result = userAccountService.findOne(1L);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findOne_notFound(){
        when(userAccountRepository.findUserAccountById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountService.findOne(0L));
    }

    @Test
    public void findAll_OK(){

        List<UserAccount> accountList = Arrays.asList(new UserAccount(),new UserAccount());
        when(userAccountRepository.findAll()).thenReturn(accountList);

        List<UserAccount> result = userAccountService.findAll();
        Assertions.assertEquals(accountList, result);
    }

    @Test
    public void registerUserAccount_OK(){

        UserAccountDTO dto = UserAccountDTO.builder().username("username").password("Password1").build();

        UserAccount expectedResult = UserAccount.builder()
                .userName(dto.getUsername())
                .password("hashedPassword")
                .isAdmin(false)
                .build();

        when(userAccountRepository.existsByUserName(dto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");
        when(userAccountRepository.save(Mockito.any(UserAccount.class))).thenReturn(expectedResult);

        UserAccount result = userAccountService.registerUserAccount(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void registerUserAccount_NullDto(){
        Assertions.assertThrows(NullEntityException.class, () -> userAccountService.registerUserAccount(null));
    }

    @Test
    public void registerUserAccount_existingUsername(){
        UserAccountDTO dto = UserAccountDTO.builder().username("username").password("Password1").build();

        when(userAccountRepository.existsByUserName(dto.getUsername())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> userAccountService.registerUserAccount(dto));
    }

    @Test
    public void registerUserAccount_InvalidPassword(){
        UserAccountDTO dto = UserAccountDTO.builder().username("username").password("pass").build();

        when(userAccountRepository.existsByUserName(dto.getUsername())).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> userAccountService.registerUserAccount(dto));
    }

    @Test
    public void update_OK(){
        UserAccount account = UserAccount.builder().userName("username").password("password1").isAdmin(false).build();

        when(userAccountRepository.existsByUserName(Mockito.anyString())).thenReturn(true);
        when(userAccountRepository.save(Mockito.any(UserAccount.class))).thenReturn(account);

        UserAccount result = userAccountService.update(account);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(account,result);
    }

    @Test
    public void update_NullAccount(){
        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountService.update(null));
    }

    @Test
    public void update_usernameDontExist(){
        UserAccount account = UserAccount.builder().userName("username").password("password1").build();

        when(userAccountRepository.existsByUserName(Mockito.anyString())).thenReturn(false);
        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountService.update(account));
    }

    @Test
    public void delete_OK(){
        Long userAccountId = 1L;
        UserAccount userAccount = new UserAccount();

        when(userAccountRepository.findUserAccountById(userAccountId)).thenReturn(Optional.of(userAccount));
        doNothing().when(userAccountRepository).delete(userAccount);

        Assertions.assertDoesNotThrow(() -> userAccountService.delete(userAccountId));
    }

    @Test
    public void delete_NullID(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userAccountService.delete(null));
    }

    @Test
    public void delete_NotFound(){
        when(userAccountRepository.findUserAccountById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountService.delete(1L));
    }

    @Test
    public void delete_AccountBeforeUser(){
        Long userAccountId = 1L;
        UserAccount userAccount = new UserAccount();
        User user = new User();
        userAccount.setUser(user);

        when(userAccountRepository.findUserAccountById(userAccountId)).thenReturn(Optional.of(userAccount));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userAccountService.delete(1L));
    }

    @Test
    public void authenticate_OK(){

        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("password").build();
        UserAccount registeredAccount = UserAccount.builder().userName("username").password("hashedPassword").isAdmin(false).build();

        when(userAccountRepository.findByUserName("username")).thenReturn(Optional.of(registeredAccount));
        when(passwordEncoder.matches(accountDTO.getPassword(), registeredAccount.getPassword())).thenReturn(true);

        Assertions.assertDoesNotThrow(()->userAccountService.authenticate(accountDTO));

        Authentication authentication = userAccountService.authenticate(accountDTO);
        Assertions.assertTrue(authentication.getAuthorities().size() == 1);
    }

    @Test
    public void authenticate_NullAccount(){
        Assertions.assertThrows(NullEntityException.class, () -> userAccountService.authenticate(null));
    }

    @Test
    public void authenticate_AccountNotFound(){
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("password").build();
        when(userAccountRepository.findByUserName("username")).thenReturn(Optional.empty());

        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountService.authenticate(accountDTO));
    }

    @Test
    public void authenticate_WrongPassword(){
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("password").build();
        UserAccount registeredAccount = UserAccount.builder().userName("username").password("hashedPassword").isAdmin(false).build();

        when(userAccountRepository.findByUserName("username")).thenReturn(Optional.of(registeredAccount));
        when(passwordEncoder.matches(accountDTO.getPassword(), registeredAccount.getPassword())).thenReturn(false);

        Assertions.assertThrows(IncorrectPasswordException.class, ()->userAccountService.authenticate(accountDTO));
    }

    @Test
    public void changePassword_OK(){

        UserAccount expectedAccount = UserAccount.builder().userName("username").password("hashedPassword").isAdmin(false).build();

        when(userAccountRepository.findUserAccountById(Mockito.anyLong())).thenReturn(Optional.of(expectedAccount));
        when(passwordEncoder.matches("oldPassword1",expectedAccount.getPassword())).thenReturn(true);

        Assertions.assertDoesNotThrow(()-> userAccountService.changePassword(1L, "oldPassword1", "newPassword1"));
    }

    @Test
    public void changePassword_Null(){
        Assertions.assertThrows(NullEntityException.class, ()-> userAccountService.changePassword(null, "password", "password"));
    }

    @Test
    public void changePassword_Invalid(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> userAccountService.changePassword(1L, "password1", "passwordwithnonumbers"));
    }

    @Test
    public void changePassword_KO(){
        UserAccount expectedAccount = UserAccount.builder().userName("username").password("hashedPassword").isAdmin(false).build();

        when(userAccountRepository.findUserAccountById(Mockito.anyLong())).thenReturn(Optional.of(expectedAccount));
        when(passwordEncoder.matches("wrongOldPassword1",expectedAccount.getPassword())).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, ()-> userAccountService.changePassword(1L, "wrongOldPassword1", "newPassword1"));
    }

    @Test
    public void changeAuthority_OK(){
        Long userAccountId = 2L;
        UserAccount account = new UserAccount();

        when(userAccountRepository.findUserAccountById(userAccountId)).thenReturn(Optional.of(account));
        Assertions.assertDoesNotThrow(()-> userAccountService.changeAuthority(userAccountId, true));
    }

    @Test
    public void changeAuthority_KO(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> userAccountService.changeAuthority(null, true));
    }

    @Test
    public void changeAuthority_NotFound(){
        Long userAccountId = 2L;
        when(userAccountRepository.findUserAccountById(userAccountId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserAccountNotFoundException.class, ()-> userAccountService.changeAuthority(userAccountId, true));
    }

    @Test
    public void getAuthorities_OK(){

        UserAccount account = UserAccount.builder().userName("username").password("password").isAdmin(true).build();
        List<GrantedAuthority> authorities = userAccountService.getAuthorities(account);
        Assertions.assertTrue(authorities.size()==2);
    }

    @Test
    public void getAuthorities_KO(){
        Assertions.assertThrows(NullEntityException.class, ()-> userAccountService.getAuthorities(null));
    }
}

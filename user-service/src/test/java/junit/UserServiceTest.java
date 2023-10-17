package junit;

import com.google.common.collect.Lists;
import com.tfg.slr.usersmicroservice.dtos.*;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.exceptions.UserNotFoundException;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.repositories.UserRepository;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.UserService;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import com.tfg.slr.usersmicroservice.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private AuthUserService authUserService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void findAll_OK(){
        List<User> expectedList = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedList);

        List<User> result = userService.findAll();
        Assertions.assertEquals(result, expectedList);
    }

    @Test
    public void findById_OK(){
        User expectedUser = new User();
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedUser,result.get());
    }

    @Test
    public void findById_KO(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> userService.findById(null));
    }

    @Test
    public void findByEmail_OK(){
        User expectedUser = new User();
        when(userRepository.findUserByEmail("testemail@gmail.com")).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findByEmail("testemail@gmail.com");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedUser,result.get());
    }

    @Test
    public void findByEmail_KO(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> userService.findByEmail(null));
    }

    @Test
    public void createAndSave_OK(){

        UserDTO userDTO = UserDTO.builder().name("Name")
                .institution("institution").workField("STEM").email("testemail@gmail.com").build();
        UserAccount account = UserAccount.builder().userName("username").build();

        when(userRepository.existsByEmail("testemail@gmail.com")).thenReturn(false);
        when(authUserService.getAuthUserAccount()).thenReturn(account);

        UserProfileDTO resultDTO = userService.createAndSave(userDTO);

        Assertions.assertDoesNotThrow(()->userService.createAndSave(userDTO));
        Assertions.assertEquals(resultDTO.getCompleteName(),userDTO.getName());
        Assertions.assertEquals(resultDTO.getUsername(), account.getUserName());
    }

    @Test
    public void createAndSave_NullEntity(){
        Assertions.assertThrows(NullEntityException.class, ()->userService.createAndSave(null));
    }
    @Test
    public void createAndSave_WrongEmail(){
        UserDTO userDTO = UserDTO.builder().name("Name")
                .institution("institution").workField("STEM").email("testemail@gmail.com").build();

        when(userRepository.existsByEmail("testemail@gmail.com")).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, ()->userService.createAndSave(userDTO));
    }

    @Test
    public void update_OK(){
        User user = User.builder().completeName("Name")
                .institution("institution").workField("STEM").email("testemail@gmail.com").build();

        when(userRepository.existsByEmail("testemail@gmail.com")).thenReturn(true);
        Assertions.assertDoesNotThrow(()->userService.update(user));
    }

    @Test
    public void update_Null(){
        Assertions.assertThrows(NullEntityException.class, ()->userService.update(null));
    }

    @Test
    public void update_NotFound(){
        User user = User.builder().completeName("Name")
                .institution("institution").workField("STEM").email("testemail@gmail.com").build();

        when(userRepository.existsByEmail("testemail@gmail.com")).thenReturn(false);
        Assertions.assertThrows(UserNotFoundException.class, ()->userService.update(user));
    }

    @Test
    public void delete_OK(){
        User user = User.builder().build();

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(()-> userService.delete(1L));
    }

    @Test
    public void delete_KO(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> userService.delete(null));
    }

    @Test
    public void getSLR_Null() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.getSLR(null));
    }

    @Test
    public void createSLR_NullDTO(){
        Assertions.assertThrows(NullEntityException.class, ()-> userService.createSLR(null, 1L));
    }

    @Test
    public void createSLR_NullId(){
        ResearcherAndSLR fullDTO = new ResearcherAndSLR();

        Assertions.assertThrows(NullEntityException.class, ()-> userService.createSLR(fullDTO, null));
    }

}

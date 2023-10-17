package com.tfg.slr.usersmicroservice.integrationTests;

import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.UserService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private  UserService userService;
    @MockBean
    private UserAccountService userAccountService;


    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_OK() throws Exception {
        User admin1 = User.builder().completeName("User Admin").email("user1@test.com").workField("Tech").institution("US").build();
        UserAccount userAccount1 = UserAccount.builder().userName("admin1").password("Valispassword1").isAdmin(true).build();
        admin1.setUserAccount(userAccount1);
        User admin2 = User.builder().completeName("Admin 2").email("user2@test.com").workField("Tech").institution("US").build();
        UserAccount userAccount2 = UserAccount.builder().userName("admin2").password("Valispassword2").isAdmin(true).build();
        admin2.setUserAccount(userAccount2);
        User user1 = User.builder().completeName("User 1").email("user@test.com").workField("Tech").institution("US").build();
        UserAccount userAccount3 = UserAccount.builder().userName("user1").password("Validpassword3").isAdmin(false).build();
        user1.setUserAccount(userAccount3);
        List<User> userList = Arrays.asList(admin1, admin2, user1);

        when(userService.findAll()).thenReturn(userList);

        mockMvc.perform(get("/api/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService, times(1)).findAll();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getALL_NoContent() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/all")).andExpect(status().isNoContent());

        verify(userService, times(1)).findAll();
    }

    @Test
    public void getAll_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/all")).andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    public void getAll_AccessDenied() throws Exception {
        mockMvc.perform(get("/api/admin/all")).andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_OK() throws Exception{

        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/api/admin/user/{id}/delete-profile", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(MessageConstants.PROFILE_DELETED));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_UserNotFound() throws Exception {
        doThrow(new IllegalArgumentException(MessageConstants.NULL_ENTITY)).when(userService).delete(anyLong());

        mockMvc.perform(delete("/api/admin/user/{id}/delete-profile", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.NULL_ENTITY));
    }
    @Test
    @WithMockUser
    public void delete_AccessDenied() throws Exception {
        mockMvc.perform(delete("/api/admin/user/{id}/delete-profile", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    public void delete_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/admin/user/{id}/delete-profile", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteAccount_OK() throws Exception{
        doNothing().when(userAccountService).delete(anyLong());

        mockMvc.perform(delete("/api/admin/user-account/{id}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(MessageConstants.ACCOUNT_DELETED));

        verify(userAccountService,times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteAccount_KO() throws Exception{
        doThrow(new IllegalArgumentException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND)).when(userAccountService).delete(1L);

        mockMvc.perform(delete("/api/admin/user-account/{id}/delete", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));
    }

    @Test
    @WithMockUser
    public void deleteAccount_AccessDenied() throws Exception {
        mockMvc.perform(delete("/api/admin/user-account/{id}/delete", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAccount_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/admin/user-account/{id}/delete", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void changeAuthorities_OK() throws Exception {
        Long userId = 2L;

        UserAccount userAccount = UserAccount.builder().userName("userToTest").password("TestPassword2").isAdmin(false).build();
        User user = User.builder().completeName("User Test").email("user@test.com").workField("Tech").institution("US").build();
        user.setUserAccount(userAccount);

        when(userService.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/admin/user/{id}/change-authority", userId)).andExpect(status().isOk())
                        .andExpect(content().string(MessageConstants.AUTHORITY_CHANGED));

        Assertions.assertTrue(userAccount.getIsAdmin());
        verify(userAccountService, times(1)).update(userAccount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void changeAuthorities_KO() throws Exception {
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/admin/user/{id}/change-authority", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.USER_NULL_NOT_FOUND));

        verifyNoInteractions(userAccountService);
    }

    @Test
    @WithMockUser
    public void changeAuthorities_AccessDenied() throws Exception {
        Long userId = 1L;
        mockMvc.perform(put("/api/admin/user/{id}/change-authority", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeAuthorities_Unauthorized() throws Exception {
        Long userId = 1L;
        mockMvc.perform(put("/api/admin/user/{id}/change-authority", userId))
                .andExpect(status().isUnauthorized());
    }

}

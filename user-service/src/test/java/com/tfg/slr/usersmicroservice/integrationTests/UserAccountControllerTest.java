package com.tfg.slr.usersmicroservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.slr.usersmicroservice.dtos.NewPasswordDTO;
import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserAccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private AuthUserService authUserService;

    @Test
    @WithMockUser
    public void getAll_OK() throws Exception{

        List<UserAccount> userAccountList = Lists.newArrayList(new UserAccount(), new UserAccount());

        when(userAccountService.findAll()).thenReturn(userAccountList);

        mockMvc.perform(get("/api/user/account/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(userAccountList.size()));
    }

    @Test
    @WithMockUser
    public void getAll_NoContent() throws Exception{

        when(userAccountService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/account/all")).andExpect(status().isNoContent());

        verify(userAccountService, times(1)).findAll();
    }

    @Test
    @WithMockUser
    public void getOneById_OK() throws Exception{
        UserAccount expectedResult = UserAccount.builder().userName("user1").password("password1").build();

        when(userAccountService.findOne(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/api/user/account/{id}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(expectedResult.getUserName()));

        verify(userAccountService, times(1)).findOne(1L);
    }

    @Test
    @WithMockUser
    public void getOneById_NotFound() throws Exception{

        when(userAccountService.findOne(1L)).thenThrow(UserAccountNotFoundException.class);

        mockMvc.perform(get("/api/user/account/{id}", 1L)).andExpect(status().isNotFound())
                .andExpect(content().string(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));

        verify(userAccountService, times(1)).findOne(1L);
    }

    @Test
    @WithMockUser
    public void updateProfile_OK() throws Exception {
        UserAccountDTO userAccountDTO = UserAccountDTO.builder().username("New username").password("Password1").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);
        when(authUserService.validatePassword(userAccountDTO.getPassword())).thenReturn(true);
        when(userAccountService.update(authUserAccount)).thenReturn(authUserAccount);

        mockMvc.perform(put("/api/user/account/update-username").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userAccountDTO))).andExpect(status().isOk())
                .andExpect(content().string(MessageConstants.USERNAME_CHANGED));

        verify(userAccountService, times(1)).update(authUserAccount);
    }

    @Test
    @WithMockUser
    public void updateProfile_HasBindingErrors() throws Exception {
        UserAccountDTO badAccountDTO = UserAccountDTO.builder().username("").password("Password1").build();

        mockMvc.perform(put("/api/user/account/update-username").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(badAccountDTO)))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Field error in object")));
    }

    @Test
    @WithMockUser
    public void updateProfile_InvalidPassword() throws Exception {
        UserAccountDTO badAccountDTO = UserAccountDTO.builder().username("valid username").password("InvalidPassword1").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);
        when(authUserService.validatePassword(badAccountDTO.getPassword())).thenReturn(false);

        mockMvc.perform(put("/api/user/account/update-username").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(badAccountDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(MessageConstants.PASSWORD_HAS_ERRORS));

        verify(authUserService, times(1)).validatePassword(badAccountDTO.getPassword());
        Mockito.verifyNoInteractions(userAccountService);
    }

    @Test
    @WithMockUser
    public void updateProfile_KO() throws Exception{
        UserAccountDTO userAccountDTO = UserAccountDTO.builder().username("New username").password("Password1").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);
        when(authUserService.validatePassword(userAccountDTO.getPassword())).thenReturn(true);
        when(userAccountService.update(authUserAccount)).thenThrow(UserAccountNotFoundException.class);

        mockMvc.perform(put("/api/user/account/update-username").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userAccountDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND));

        verify(authUserService, times(1)).getAuthUserAccount();
        verify(authUserService, times(1)).validatePassword(userAccountDTO.getPassword());
        verify(userAccountService, times(1)).update(authUserAccount);
    }

    @Test
    @WithAnonymousUser
    public void updateProfile_Unauthorized() throws Exception {
        UserAccountDTO userAccountDTO = UserAccountDTO.builder().username("valid username").password("InvalidPassword1").build();

        mockMvc.perform(put("/api/user/account/update-username").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userAccountDTO)))
                        .andExpect(status().isUnauthorized());

        verifyNoInteractions(authUserService);
        verifyNoInteractions(userAccountService);
    }

    @Test
    @WithMockUser
    public void changePassword_OK() throws Exception {
        NewPasswordDTO passwordDTO = NewPasswordDTO.builder().username("authUser").oldPassword("Password1").newPassword("Password2").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);

        mockMvc.perform(put("/api/user/account/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordDTO)))
                        .andExpect(status().isOk())
                        .andExpect(content().string(MessageConstants.PASSWORD_CHANGED));

        verify(authUserService, times(1)).getAuthUserAccount();
        //Given that changePassword returns void, we only need to verify that the method is called and no Exception is thrown
        verify(userAccountService, times(1)).changePassword(authUserAccount.getId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
    }

    @Test
    @WithAnonymousUser
    public void changePassword_Unauthorized() throws Exception {
        NewPasswordDTO passwordDTO = NewPasswordDTO.builder().username("authUser").oldPassword("Password1").newPassword("Password2").build();

        mockMvc.perform(put("/api/user/account/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void changePassword_HasBindingErrors() throws Exception {
        NewPasswordDTO passwordDTO = NewPasswordDTO.builder().username("authUser").oldPassword("Password1").newPassword("password").build();

        mockMvc.perform(put("/api/user/account/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordDTO)))
                        .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Field error in object")));
    }

    @Test
    @WithMockUser
    public void changePassword_WrongUsername() throws Exception {
        NewPasswordDTO passwordDTO = NewPasswordDTO.builder().username("wrongUser").oldPassword("Password1").newPassword("Password2").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);

        mockMvc.perform(put("/api/user/account/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(MessageConstants.WRONG_USERNAME));

        verify(authUserService, times(1)).getAuthUserAccount();
    }

    @Test
    @WithMockUser
    public void changePassword_KO() throws Exception {
        NewPasswordDTO passwordDTO = NewPasswordDTO.builder().username("authUser").oldPassword("Password1").newPassword("Password2").build();
        UserAccount authUserAccount = UserAccount.builder().userName("authUser").password("Password1").build();

        when(authUserService.getAuthUserAccount()).thenReturn(authUserAccount);

        doThrow(new IllegalArgumentException("Something went wrong"))
                .when(userAccountService).changePassword(authUserAccount.getId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());

        mockMvc.perform(put("/api/user/account/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Something went wrong"));

        verify(authUserService, times(1)).getAuthUserAccount();
        verify(userAccountService, times(1)).changePassword(authUserAccount.getId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
    }

}

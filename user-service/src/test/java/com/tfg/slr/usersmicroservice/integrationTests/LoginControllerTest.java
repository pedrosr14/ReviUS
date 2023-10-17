package com.tfg.slr.usersmicroservice.integrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.slr.usersmicroservice.controllers.LoginController;
import com.tfg.slr.usersmicroservice.dtos.TokenDTO;
import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private AuthUserService authUserService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    public void registerUserAccount_OK() throws Exception{

        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("Password1").build();
        UserAccount account = UserAccount.builder().id(23L).userName("username").password("$2a$12$cG0FC1yd71zCz7A2Ix2kg.iEfuKRvM8tmLRaYTrBgMR2LgKPj4O.O").isAdmin(false).build();

        when(userAccountService.registerUserAccount(accountDTO)).thenReturn(account);

        mockMvc.perform(post("/api/login/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isCreated());

        verify(userAccountService, times(1)).registerUserAccount(accountDTO);
    }

    @Test
    public void registerUserAccount_BindingErrors() throws Exception {
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("").password("badpassword").build();

        mockMvc.perform(post("/api/login/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isBadRequest());

        verify(userAccountService, times(0)).registerUserAccount(accountDTO);
    }

    @Test
    public void registerUserAccount_KO() throws Exception {
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("Password1").build();
        UserAccount account = UserAccount.builder().id(23L).userName("username").password("$2a$12$cG0FC1yd71zCz7A2Ix2kg.iEfuKRvM8tmLRaYTrBgMR2LgKPj4O.O").isAdmin(false).build();

        when(userAccountService.registerUserAccount(accountDTO)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post("/api/login/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isBadRequest());

        verify(userAccountService, times(1)).registerUserAccount(accountDTO);
    }

    @Test
    public void login_OK() throws Exception{
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("username").password("Password1").build();
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication auth = new UsernamePasswordAuthenticationToken(accountDTO, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userAccountService.authenticate(accountDTO)).thenReturn(auth);

        String token = "thisIsAJWTToken";
        when(jwtProvider.generateToken(auth)).thenReturn(token);

        mockMvc.perform(post("/api/login/").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").value(token));;
    }

    @Test
    public void login_Unauthorized() throws Exception {
        UserAccountDTO accountDTO = UserAccountDTO.builder().username("invalidUsername").password("wrongPassword").build();

        when(userAccountService.authenticate(accountDTO)).thenReturn(null);

        mockMvc.perform(post("/api/login/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(MessageConstants.INVALID_USERNAME_OR_PASSWORD));
    }

    @Test
    public void validate_OK() throws Exception {
        String token = "originalToken";
        TokenDTO tokenDTO = TokenDTO.builder().token(token).build();

        when(authUserService.validate(token)).thenReturn(tokenDTO);
        mockMvc.perform(post("/api/login/validate").param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    public void validate_KO() throws Exception {
        String token = "originalToken";

        when(authUserService.validate(token)).thenReturn(null);
        mockMvc.perform(post("/api/login/validate").param("token", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validate_InvalidToken() throws Exception {
        String token = "invalidToken";

        when(authUserService.validate(token)).thenThrow(IllegalArgumentException.class);
        mockMvc.perform(post("/api/login/validate").param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.INVALID_TOKEN));
    }

}
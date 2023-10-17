package com.tfg.slr.usersmicroservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.slr.usersmicroservice.dtos.*;
import com.tfg.slr.usersmicroservice.exceptions.LoadProfileException;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.UserService;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthUserService authUserService;
    @MockBean
    private UserAccountService userAccountService;

    @Test
    @WithMockUser
    public void getAll_OK() throws Exception {
        User user1 = User.builder().id(1L).completeName("User 1").email("user1@gmail.com").build();
        User user2 = User.builder().id(2L).completeName("User 2").email("user2@gmail.com").build();

        List<User> expectedList = Lists.newArrayList(user1, user2);
        when(userService.findAll()).thenReturn(expectedList);

        mockMvc.perform(get("/api/user/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                //This will tell us if the operation went good because User has username but UserDTO has name
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[0].email").value("user1@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User 2"))
                .andExpect(jsonPath("$[1].email").value("user2@gmail.com"));

        verify(userService, times(1)).findAll();
    }

    @Test
    @WithAnonymousUser
    public void getAll_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/all")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getAll_KO() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/all")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void getUser_OK() throws Exception {
        User user = User.builder().id(1L).completeName("User 1").email("user@gmail.com").institution("US").build();

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/{userId}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User 1"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    public void getUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/{userId}", 1L)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getUser_NotFound() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/{userId}", 99L)).andExpect(status().isNotFound());

        verify(userService, times(1)).findById(99L);
    }

    @Test
    @WithMockUser
    public void createProfile_OK() throws Exception {
        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();
        UserProfileDTO profileDTO = UserProfileDTO.builder().completeName("User 1").workField("TI").institution("US")
                .email("user1@us.es").userAccount_id(1L).username("user_1").build();

        when(userService.createAndSave(userDTO)).thenReturn(profileDTO); //if DTO doesn't have equals and hashCode, this returns null

        mockMvc.perform(post("/api/user/create-profile").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(profileDTO.getUsername()));

        verify(userService, times(1)).createAndSave(userDTO);
    }

    @Test
    @WithAnonymousUser
    public void createProfile_Unauthorized() throws Exception {
        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();

        mockMvc.perform(post("/api/user/create-profile").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createProfile_HasBindingErrors() throws Exception {

        UserDTO userDTO_NoWorkField = UserDTO.builder().name("User 1").email("user1@us.es").institution("US").build();

        mockMvc.perform(post("/api/user/create-profile").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO_NoWorkField))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Field error in object")));

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    public void createProfile_KO() throws Exception {
        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();
        UserProfileDTO profileDTO = UserProfileDTO.builder().completeName("User 1").workField("TI").institution("US")
                .email("user1@us.es").userAccount_id(1L).username("user_1").build();

        doThrow(new IllegalArgumentException(MessageConstants.EXISTING_EMAIL)).when(userService).createAndSave(userDTO);

        mockMvc.perform(post("/api/user/create-profile").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.EXISTING_EMAIL));

        verify(userService, times(1)).createAndSave(userDTO);
    }

    @Test
    @WithMockUser
    public void updateProfile_OK() throws Exception{
        Long userId = 1L;
        User user = User.builder().completeName("User 1").email("user1@outlook.es").build();
        UserAccount userAccount = UserAccount.builder().id(23L).userName("user1").password("ValidPassword1").build();
        user.setUserAccount(userAccount);

        UserAccount principalUserAccount =  UserAccount.builder().id(23L).userName("user1").password("ValidPassword1").build();
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(authUserService.getAuthUserAccount()).thenReturn(principalUserAccount);

        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();
        mockMvc.perform(put("/api/user/{id}/update",userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.userAccount_id").value(principalUserAccount.getId()));

        verify(userService, times(1)).update(user);
    }

    @Test
    @WithAnonymousUser
    public void updateProfile_Unauthorized() throws Exception{
        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();
        mockMvc.perform(put("/api/user/{id}/update",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateProfile_hasBindingErrors() throws Exception{
        UserDTO userDTO_badEmail = UserDTO.builder().name("User 1").email("user1.es").workField("TI").institution("US").build();

        mockMvc.perform(put("/api/user/{id}/update",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO_badEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Field error in object")));
    }

    @Test
    @WithMockUser
    public void updateProfile_UserNotFound() throws Exception {
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();

        when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/user/{id}/update",userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageConstants.USER_NOT_FOUND)));

        verifyNoInteractions(authUserService);
    }

    @Test
    @WithMockUser
    public void updateProfile_WrongId() throws Exception{
        Long userId = 1L;
        User user = User.builder().completeName("User 1").email("user1@outlook.es").build();
        UserAccount userAccount = UserAccount.builder().id(25L).userName("user1").password("ValidPassword1").build();
        user.setUserAccount(userAccount);

        UserAccount principalUserAccount =  UserAccount.builder().id(23L).userName("user1").password("ValidPassword1").build();
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(authUserService.getAuthUserAccount()).thenReturn(principalUserAccount);

        UserDTO userDTO = UserDTO.builder().name("User 1").email("user1@us.es").workField("TI").institution("US").build();
        mockMvc.perform(put("/api/user/{id}/update",userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("El ID proporcionado no coincide con el de user1")));

        verify(userService, times(0)).update(user);
    }

    @Test
    @WithMockUser
    public void delete_OK() throws Exception{
        Long userID = 2L;

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        mockMvc.perform(delete("/api/user/{id}/delete-profile", userID)).andExpect(status().isOk())
                .andExpect(content().string(containsString(MessageConstants.PROFILE_DELETED)));

        verify(userService, times(1)).delete(userID);
    }

    @Test
    @WithMockUser
    public void delete_KO() throws Exception{
        Long userID = 2L;

        doThrow(new IllegalArgumentException("Impossible to delete the profile")).when(userService).delete(userID);

        mockMvc.perform(delete("/api/user/{id}/delete-profile", userID)).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageConstants.DELETING_ERROR)));

        verify(userService, times(1)).delete(userID);
    }

    @Test
    @WithAnonymousUser
    public void delete_Unauthorized() throws Exception {

        mockMvc.perform(delete("/api/user/{id}/delete-profile", 2L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getSLRs_OK() throws Exception {
        Long userID = 3L;
        SLRDTO reviewDTO1 = SLRDTO.builder().title("Review 1").build();
        SLRDTO reviewDTO2 = SLRDTO.builder().title("Review 2").build();
        List<SLRDTO> expectedResult = Lists.newArrayList(reviewDTO1, reviewDTO2);

        when(userService.getSLR(userID)).thenReturn(expectedResult);

        mockMvc.perform(get("/api/user/{userId}/my-reviews", userID)).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Review 1"))
                .andExpect(jsonPath("$[1].title").value("Review 2"));

        verify(userService, times(1)).getSLR(userID);
    }

    @Test
    @WithAnonymousUser
    public void getSLR_Unauthorized() throws Exception {
        Long userID = 3L;
        mockMvc.perform(get("/api/user/{userId}/my-reviews", userID)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getSLR_NoContent() throws Exception {
        Long userID = 3L;
        when(userService.getSLR(userID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/{userId}/my-reviews", userID)).andExpect(status().isNoContent());

        verify(userService,times(1)).getSLR(userID);
    }

    @Test
    @WithMockUser
    public void getSLR_KO() throws Exception {
        Long userID = 3L;
        doThrow(new IllegalArgumentException("Error intentando obtener las revisiones")).when(userService).getSLR(userID);

        mockMvc.perform(get("/api/user/{userId}/my-reviews", userID)).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error intentando obtener las revisiones")));

        verify(userService,times(1)).getSLR(userID);
    }

    @Test
    @WithMockUser
    public void createFromUser_OK() throws Exception {
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder().name("User Test").institution("US").workField("IT").email("testuser@us.es").build();
        SLRDTO slrdto = SLRDTO.builder().title("Test Review").description("This is a test").workField("IT")
                .objective("Test the api").publicVisibility(true).build();
        ResearcherAndSLR researcherAndSLR = new ResearcherAndSLR(userDTO,slrdto);

        when(userService.createSLR(researcherAndSLR, userId)).thenReturn(slrdto);

        mockMvc.perform(post("/api/user/{userId}/review/create", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(researcherAndSLR)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(slrdto.getTitle()))
                .andExpect(jsonPath("$.objective").value(slrdto.getObjective()))
                .andExpect(jsonPath("$.publicVisibility").value(slrdto.getPublicVisibility()));

        verify(userService,times(1)).createSLR(researcherAndSLR, userId);
    }

    @Test
    @WithAnonymousUser
    public void createFromUser_Unauthorized() throws Exception {
        Long userId = 1L;
        ResearcherAndSLR researcherAndSLR = new ResearcherAndSLR();

        mockMvc.perform(post("/api/user/{userId}/review/create", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(researcherAndSLR)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createFromUser_withBindingErrors() throws Exception {
            Long userId = 1L;
            UserDTO emptyUserDTO = UserDTO.builder().build();
            SLRDTO slrdto = SLRDTO.builder().title("Test Review").description("This is a test").workField("IT")
                    .objective("Test the api").publicVisibility(true).build();
            ResearcherAndSLR researcherAndSLR = new ResearcherAndSLR(emptyUserDTO,slrdto);

            when(userService.createSLR(researcherAndSLR, userId)).thenReturn(slrdto);

            mockMvc.perform(post("/api/user/{userId}/review/create",userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(researcherAndSLR)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Field error in object")));

            verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    public void createFromUser_KO() throws Exception {
        Long userId = 1L;
        ResearcherAndSLR researcherAndSLR = new ResearcherAndSLR();

        doThrow(new NullEntityException(MessageConstants.NULL_ENTITY)).when(userService).createSLR(researcherAndSLR, userId);

        mockMvc.perform(post("/api/user/{userId}/review/create",userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(researcherAndSLR)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MessageConstants.NULL_ENTITY)));

        verify(userService,times(1)).createSLR(researcherAndSLR, userId);
    }


    @Test
    @WithMockUser
    public void getProfile_OK() throws Exception {
        UserAccount userAccount = UserAccount.builder().userName("usertest").password("ValidPassword2").build();
        User user = User.builder().completeName("Testing User").email("user@test.es").institution("US").workField("Tech").build();
        userAccount.setUser(user);

        when(authUserService.getAuthUserAccount()).thenReturn(userAccount);

        mockMvc.perform(get("/api/user/my-profile")).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userAccount.getUserName()))
                .andExpect(jsonPath("$.completeName").value(user.getCompleteName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(authUserService, times(1)).getAuthUserAccount();
    }

    @Test
    @WithMockUser
    public void getProfile_KO() throws Exception {
        when(authUserService.getAuthUserAccount()).thenThrow(new LoadProfileException(MessageConstants.ERROR_LOADING_PROFILE));

        mockMvc.perform(get("/api/user/my-profile"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(MessageConstants.ERROR_LOADING_PROFILE));
    }

    @Test
    @WithAnonymousUser
    public void getProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/my-profile"))
                .andExpect(status().isUnauthorized());
    }
}

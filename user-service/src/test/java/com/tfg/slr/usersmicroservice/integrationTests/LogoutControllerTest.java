package com.tfg.slr.usersmicroservice.integrationTests;

import com.tfg.slr.usersmicroservice.security.TokenBlacklistService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenBlacklistService blacklistService;

    @Test
    @WithMockUser
    public void logout_OK() throws Exception {
        String token = "Bearer LogoutToken";

        doNothing().when(blacklistService).addToBlacklist("LogoutToken");

        mockMvc.perform(post("/api/logout/").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string(MessageConstants.LOGOUT_SUCCESS));

        verify(blacklistService, times(1)).addToBlacklist("LogoutToken");
    }

    @Test
    @WithMockUser
    public void logout_KO_NoToken() throws Exception {

        String noToken = "";

        mockMvc.perform(post("/api/logout/").header("Authorization", noToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.LOGOUT_FAILED));

        verify(blacklistService, times(0)).addToBlacklist("LogoutToken");

    }

    @Test
    @WithMockUser
    public void logout_KO() throws Exception {

        //This time we don't add header
        mockMvc.perform(post("/api/logout/"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(MessageConstants.LOGOUT_FAILED));

        Mockito.verifyNoInteractions(blacklistService);
    }
}

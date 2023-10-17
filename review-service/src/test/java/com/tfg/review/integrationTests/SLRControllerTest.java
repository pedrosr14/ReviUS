package com.tfg.review.integrationTests;

import com.tfg.review.models.SLR;
import com.tfg.review.services.SLRService;
import org.assertj.core.internal.Dates;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SLRControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private SLRService slrService;

    @Test
    @WithMockUser
    public void getAll_OK() throws Exception {
        SLR slr1 = SLR.builder().title("SLR Test1").objective("Testing").publicVisibility(true).description("This is a test").workField("TI")
                .initDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-08-30 13:32:58")).build();
        SLR slr2 = SLR.builder().title("SLR Test2").objective("Testing").publicVisibility(false).description("This is a test")
                .workField("TI").initDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-08-30 13:52:08")).build();

        List<SLR> slrList = Lists.newArrayList(slr1, slr2);
        when(slrService.findAll()).thenReturn(slrList);

        mockMvc.perform(get("/api/review/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].publicVisibility").value(true))
                .andExpect(jsonPath("$[1].publicVisibility").value(false));
    }
}

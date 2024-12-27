package com.sparta.fritown;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoundControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRoundsByMatchId() throws Exception {
        mockMvc.perform(get("/match/12/round")
                        .with(user("testUser").roles("USER")) // 사용자 인증 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 기대 상태 코드 200
                .andExpect(jsonPath("$[0].roundNum").value(1))
                .andExpect(jsonPath("$[0].kcal").value(150))
                .andExpect(jsonPath("$[1].roundNum").value(2))
                .andExpect(jsonPath("$[1].kcal").value(200));
    }
}

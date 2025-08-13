package com.example.bankcards;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseTestConfig {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Утилита для выполнения логина и получения JWT токена.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return JWT токен
     * @throws Exception при ошибках MockMvc
     */

    protected String obtainAccessToken(String username, String password) throws Exception {
        String loginJson = objectMapper.writeValueAsString(
                new LoginRequest(username, password)
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        // Предполагаем, что ответ JSON в формате { "token": "..." }
        return objectMapper.readTree(response).get("token").asText();
    }

    // Внутренний класс для запроса логина (можно заменить на DTO из проекта)
    private static class LoginRequest {
        public String username;
        public String password;
        public LoginRequest(String u, String p) {
            this.username = u;
            this.password = p;
        }
    }

}

package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.UpdateChildRequest;
import com.github.synt3se.dto.request.UpdateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/user/me")
    class GetCurrentUser {

        @Test
        @DisplayName("Родитель получает свой профиль")
        void getMe_AsParent_ReturnsProfile() throws Exception {
            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("parent@test.com"))
                    .andExpect(jsonPath("$.fullName").value("Тестовый Родитель"))
                    .andExpect(jsonPath("$.role").value("PARENT"));
        }

        @Test
        @DisplayName("Учитель получает свой профиль")
        void getMe_AsTeacher_ReturnsProfile() throws Exception {
            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("teacher@test.com"))
                    .andExpect(jsonPath("$.role").value("TEACHER"));
        }

        @Test
        @DisplayName("Без токена возвращает 403")
        void getMe_NoToken_Returns403() throws Exception {
            mockMvc.perform(get("/api/user/me"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("С невалидным токеном возвращает 403")
        void getMe_InvalidToken_Returns403() throws Exception {
            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/user/me")
    class UpdateCurrentUser {

        @Test
        @DisplayName("Обновление профиля")
        void updateMe_Success() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest();
            request.setFullName("Обновлённое Имя");
            request.setPhone("+7 999 999-99-99");

            mockMvc.perform(put("/api/user/me")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Обновлённое Имя"))
                    .andExpect(jsonPath("$.phone").value("+7 999 999-99-99"));
        }

        @Test
        @DisplayName("Частичное обновление — только имя")
        void updateMe_PartialUpdate() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest();
            request.setFullName("Только Имя");

            mockMvc.perform(put("/api/user/me")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Только Имя"))
                    .andExpect(jsonPath("$.phone").value("+7 999 222-22-22"));
        }
    }
}

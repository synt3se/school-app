package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.LoginRequest;
import com.github.synt3se.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Успешный вход возвращает токен и данные пользователя")
        void login_Success() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("parent@test.com");
            request.setPassword("password");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.user.email").value("parent@test.com"))
                    .andExpect(jsonPath("$.user.role").value("PARENT"));
        }

        @Test
        @DisplayName("Неверный пароль возвращает 400")
        void login_WrongPassword_Returns400() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("parent@test.com");
            request.setPassword("wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Неверный email или пароль"));
        }

        @Test
        @DisplayName("Несуществующий email возвращает 400")
        void login_NonExistentEmail_Returns400() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("nonexistent@test.com");
            request.setPassword("password");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Неверный email или пароль"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Успешная регистрация возвращает ID пользователя")
        void register_Success() throws Exception {
            RegisterRequest.ChildData childData = new RegisterRequest.ChildData();
            childData.setFullName("Новый Ребёнок");
            childData.setBirthDate(LocalDate.now().minusYears(8));

            RegisterRequest request = new RegisterRequest();
            request.setFullName("Новый Родитель");
            request.setEmail("newparent@test.com");
            request.setPhone("+7 999 333-33-33");
            request.setPassword("password123");
            request.setBranchId(testBranch.getId());
            request.setChild(childData);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").isNotEmpty());
        }

        @Test
        @DisplayName("Регистрация с существующим email возвращает 409")
        void register_DuplicateEmail_Returns409() throws Exception {
            RegisterRequest.ChildData childData = new RegisterRequest.ChildData();
            childData.setFullName("Ребёнок");
            childData.setBirthDate(LocalDate.now().minusYears(8));

            RegisterRequest request = new RegisterRequest();
            request.setFullName("Родитель");
            request.setEmail("parent@test.com"); // Уже существует
            request.setPhone("+7 999 444-44-44");
            request.setPassword("password123");
            request.setBranchId(testBranch.getId());
            request.setChild(childData);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("уже существует")));
        }

        @Test
        @DisplayName("Регистрация с несуществующим филиалом возвращает 404")
        void register_NonExistentBranch_Returns404() throws Exception {
            RegisterRequest.ChildData childData = new RegisterRequest.ChildData();
            childData.setFullName("Ребёнок");
            childData.setBirthDate(LocalDate.now().minusYears(8));

            RegisterRequest request = new RegisterRequest();
            request.setFullName("Родитель");
            request.setEmail("new@test.com");
            request.setPhone("+7 999 555-55-55");
            request.setPassword("password123");
            request.setBranchId(java.util.UUID.randomUUID());
            request.setChild(childData);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Филиал")));
        }

        @Test
        @DisplayName("Регистрация с невалидными данными возвращает 400")
        void register_InvalidData_Returns400() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setFullName(""); // Пустое имя
            request.setEmail("invalid-email"); // Невалидный email
            request.setPassword("123"); // Слишком короткий пароль

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details").isNotEmpty());
        }
    }
}

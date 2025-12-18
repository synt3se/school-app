package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.UpdateChildRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChildControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/child")
    class GetChild {

        @Test
        @DisplayName("Родитель получает данные ребёнка")
        void getChild_Success() throws Exception {
            mockMvc.perform(get("/api/child")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Тестовый Ребёнок"))
                    .andExpect(jsonPath("$.courses", hasSize(1)))
                    .andExpect(jsonPath("$.courses[0].name").value("Тестовый курс"));
        }

        @Test
        @DisplayName("Учитель получает 403")
        void getChild_AsTeacher_Returns403() throws Exception {
            mockMvc.perform(get("/api/child")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/child")
    class UpdateChild {

        @Test
        @DisplayName("Обновление данных ребёнка")
        void updateChild_Success() throws Exception {
            UpdateChildRequest request = new UpdateChildRequest();
            request.setFullName("Новое Имя Ребёнка");
            request.setBirthDate(LocalDate.now().minusYears(9));

            mockMvc.perform(put("/api/child")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Новое Имя Ребёнка"));
        }
    }
}

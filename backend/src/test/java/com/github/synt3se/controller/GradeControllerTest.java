package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.GradeRequest;
import com.github.synt3se.entity.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GradeControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/grades")
    class GetGrades {

        @Test
        @DisplayName("Родитель получает оценки ребёнка")
        void getGrades_AsParent_ReturnsGrades() throws Exception {
            // Создаём оценку
            Grade grade = Grade.builder()
                    .child(testChild)
                    .lesson(testLesson)
                    .teacher(teacherUser)
                    .value(5)
                    .comment("Отлично!")
                    .build();
            gradeRepository.save(grade);

            mockMvc.perform(get("/api/grades")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].value").value(5))
                    .andExpect(jsonPath("$[0].comment").value("Отлично!"));
        }

        @Test
        @DisplayName("Учитель получает 403")
        void getGrades_AsTeacher_Returns403() throws Exception {
            mockMvc.perform(get("/api/grades")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/grades/course/{courseId}")
    class GetGradesByCourse {

        @Test
        @DisplayName("Родитель получает оценки по курсу")
        void getGradesByCourse_Success() throws Exception {
            // Создаём оценку
            Grade grade = Grade.builder()
                    .child(testChild)
                    .lesson(testLesson)
                    .teacher(teacherUser)
                    .value(4)
                    .build();
            gradeRepository.save(grade);

            mockMvc.perform(get("/api/grades/course/" + testCourse.getId())
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].value").value(4));
        }

        @Test
        @DisplayName("Неверный UUID возвращает 400")
        void getGradesByCourse_InvalidUuid_Returns400() throws Exception {
            mockMvc.perform(get("/api/grades/course/invalid-uuid")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("неверный формат")));
        }
    }

    @Nested
    @DisplayName("POST /api/grades/lesson/{lessonId}")
    class CreateGrade {

        @Test
        @DisplayName("Учитель выставляет оценку")
        void createGrade_Success() throws Exception {
            GradeRequest request = new GradeRequest();
            request.setChildId(testChild.getId());
            request.setValue(5);
            request.setComment("Молодец!");

            mockMvc.perform(post("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + teacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.value").value(5))
                    .andExpect(jsonPath("$.comment").value("Молодец!"))
                    .andExpect(jsonPath("$.teacher").value("Тестовый Учитель"));
        }

        @Test
        @DisplayName("Повторная оценка возвращает 409")
        void createGrade_AlreadyGraded_Returns409() throws Exception {
            // Создаём оценку
            Grade grade = Grade.builder()
                    .child(testChild)
                    .lesson(testLesson)
                    .teacher(teacherUser)
                    .value(4)
                    .build();
            gradeRepository.save(grade);

            GradeRequest request = new GradeRequest();
            request.setChildId(testChild.getId());
            request.setValue(5);

            mockMvc.perform(post("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + teacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Оценка уже выставлена"));
        }

        @Test
        @DisplayName("Оценка на чужом занятии возвращает 403")
        void createGrade_NotYourLesson_Returns403() throws Exception {
            // Создаём другого учителя
            var anotherTeacher = userRepository.save(
                    com.github.synt3se.entity.User.builder()
                            .fullName("Другой Учитель")
                            .email("another@test.com")
                            .phone("+7 999 666-66-66")
                            .password(passwordEncoder.encode("password"))
                            .role(com.github.synt3se.entity.Role.TEACHER)
                            .branch(testBranch)
                            .build());

            String anotherTeacherToken = jwtTokenProvider.generateToken(
                    anotherTeacher.getId(), anotherTeacher.getEmail(), String.valueOf(anotherTeacher.getRole()));

            GradeRequest request = new GradeRequest();
            request.setChildId(testChild.getId());
            request.setValue(5);

            mockMvc.perform(post("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + anotherTeacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Вы не ведёте это занятие"));
        }

        @Test
        @DisplayName("Невалидная оценка возвращает 400")
        void createGrade_InvalidValue_Returns400() throws Exception {
            GradeRequest request = new GradeRequest();
            request.setChildId(testChild.getId());
            request.setValue(10); // Должно быть 1-5

            mockMvc.perform(post("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + teacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details.value").exists());
        }

        @Test
        @DisplayName("Родитель не может выставлять оценки")
        void createGrade_AsParent_Returns403() throws Exception {
            GradeRequest request = new GradeRequest();
            request.setChildId(testChild.getId());
            request.setValue(5);

            mockMvc.perform(post("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/grades/lesson/{lessonId}")
    class GetGradesByLesson {

        @Test
        @DisplayName("Учитель получает оценки по занятию")
        void getGradesByLesson_Success() throws Exception {
            // Создаём оценку
            Grade grade = Grade.builder()
                    .child(testChild)
                    .lesson(testLesson)
                    .teacher(teacherUser)
                    .value(5)
                    .build();
            gradeRepository.save(grade);

            mockMvc.perform(get("/api/grades/lesson/" + testLesson.getId())
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].value").value(5));
        }
    }
}

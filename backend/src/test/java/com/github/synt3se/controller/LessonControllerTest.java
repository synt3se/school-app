package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.AttendanceRequest;
import com.github.synt3se.dto.request.RescheduleRequest;
import com.github.synt3se.entity.Attendance;
import com.github.synt3se.entity.Lesson;
import com.github.synt3se.entity.LessonStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LessonControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/lessons/upcoming")
    class GetUpcoming {

        @Test
        @DisplayName("Родитель получает список ближайших занятий")
        void getUpcoming_AsParent_ReturnsLessons() throws Exception {
            mockMvc.perform(get("/api/lessons/upcoming")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[0].id").value(testLesson.getId().toString()))
                    .andExpect(jsonPath("$[0].topic").value("Тестовое занятие"))
                    .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
        }

        @Test
        @DisplayName("Без токена возвращает 403")
        void getUpcoming_NoToken_Returns403() throws Exception {
            mockMvc.perform(get("/api/lessons/upcoming"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Учитель получает 403")
        void getUpcoming_AsTeacher_Returns403() throws Exception {
            mockMvc.perform(get("/api/lessons/upcoming")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/lessons/{id}/cancel")
    class CancelLesson {

        @Test
        @DisplayName("Успешная отмена занятия")
        void cancel_Success() throws Exception {
            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testLesson.getId().toString()));

            // Проверяем, что attendance обновился
            Attendance attendance = attendanceRepository
                    .findByLessonIdAndChildId(testLesson.getId(), testChild.getId())
                    .orElseThrow();
            assertFalse(attendance.getPresent());
        }

        @Test
        @DisplayName("Отменённое занятие не появляется в upcoming")
        void cancel_RemovedFromUpcoming() throws Exception {
            // Отменяем
            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk());

            // Проверяем, что занятие исчезло из upcoming
            mockMvc.perform(get("/api/lessons/upcoming")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id=='" + testLesson.getId() + "')]").doesNotExist());
        }

        @Test
        @DisplayName("Повторная отмена возвращает 409")
        void cancel_AlreadyCancelled_Returns409() throws Exception {
            // Первая отмена
            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk());

            // Повторная отмена
            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Посещаемость уже отмечена"));
        }

        @Test
        @DisplayName("Отмена несуществующего занятия возвращает 404")
        void cancel_NonExistentLesson_Returns404() throws Exception {
            mockMvc.perform(post("/api/lessons/" + UUID.randomUUID() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Отмена занятия, на которое ребёнок не записан, возвращает ошибку")
        void cancel_NotEnrolled_ReturnsBadRequest() throws Exception {
            // Создаём занятие без записи ребёнка
            Lesson anotherLesson = Lesson.builder()
                    .startTime(LocalDateTime.now().plusDays(2))
                    .endTime(LocalDateTime.now().plusDays(2).plusHours(1))
                    .course(testCourse)
                    .branch(testBranch)
                    .teacher(teacherUser)
                    .status(LessonStatus.SCHEDULED)
                    .topic("Другое занятие")
                    .build();
            lessonRepository.save(anotherLesson);

            mockMvc.perform(post("/api/lessons/" + anotherLesson.getId() + "/cancel")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/lessons/reschedule")
    class RescheduleLesson {

        @Test
        @DisplayName("Успешный перенос занятия")
        void reschedule_Success() throws Exception {
            // Создаём занятие для переноса
            Lesson targetLesson = Lesson.builder()
                    .startTime(LocalDateTime.now().plusDays(3))
                    .endTime(LocalDateTime.now().plusDays(3).plusHours(1))
                    .course(testCourse)
                    .branch(testBranch)
                    .teacher(teacherUser)
                    .status(LessonStatus.SCHEDULED)
                    .topic("Целевое занятие")
                    .build();
            lessonRepository.save(targetLesson);

            RescheduleRequest request = new RescheduleRequest();
            request.setFromLessonId(testLesson.getId());
            request.setToLessonId(targetLesson.getId());

            mockMvc.perform(post("/api/lessons/reschedule")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(targetLesson.getId().toString()));

            // Проверяем, что создалась новая запись
            assertTrue(attendanceRepository
                    .findByLessonIdAndChildId(targetLesson.getId(), testChild.getId())
                    .isPresent());
        }

        @Test
        @DisplayName("Перенос на другой курс возвращает 400")
        void reschedule_DifferentCourse_Returns400() throws Exception {
            // Создаём другой курс и занятие
            var anotherCourse = courseRepository.save(
                    com.github.synt3se.entity.Course.builder().name("Другой курс").build());

            Lesson targetLesson = Lesson.builder()
                    .startTime(LocalDateTime.now().plusDays(3))
                    .endTime(LocalDateTime.now().plusDays(3).plusHours(1))
                    .course(anotherCourse)
                    .branch(testBranch)
                    .teacher(teacherUser)
                    .status(LessonStatus.SCHEDULED)
                    .build();
            lessonRepository.save(targetLesson);

            RescheduleRequest request = new RescheduleRequest();
            request.setFromLessonId(testLesson.getId());
            request.setToLessonId(targetLesson.getId());

            mockMvc.perform(post("/api/lessons/reschedule")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("другой курс")));
        }

        @Test
        @DisplayName("Перенос на занятие, где уже записан, возвращает 409")
        void reschedule_AlreadyEnrolled_Returns409() throws Exception {
            // Создаём занятие и записываем ребёнка
            Lesson targetLesson = Lesson.builder()
                    .startTime(LocalDateTime.now().plusDays(3))
                    .endTime(LocalDateTime.now().plusDays(3).plusHours(1))
                    .course(testCourse)
                    .branch(testBranch)
                    .teacher(teacherUser)
                    .status(LessonStatus.SCHEDULED)
                    .build();
            lessonRepository.save(targetLesson);

            attendanceRepository.save(Attendance.builder()
                    .lesson(targetLesson)
                    .child(testChild)
                    .build());

            RescheduleRequest request = new RescheduleRequest();
            request.setFromLessonId(testLesson.getId());
            request.setToLessonId(targetLesson.getId());

            mockMvc.perform(post("/api/lessons/reschedule")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("уже записан")));
        }
    }

    @Nested
    @DisplayName("GET /api/lessons/today (Teacher)")
    class GetTodayForTeacher {

        @Test
        @DisplayName("Учитель получает занятия на сегодня")
        void getToday_AsTeacher_ReturnsLessons() throws Exception {
            // Создаём занятие на сегодня
            Lesson todayLesson = Lesson.builder()
                    .startTime(LocalDateTime.now().withHour(14).withMinute(0))
                    .endTime(LocalDateTime.now().withHour(15).withMinute(30))
                    .course(testCourse)
                    .branch(testBranch)
                    .teacher(teacherUser)
                    .status(LessonStatus.SCHEDULED)
                    .topic("Занятие сегодня")
                    .build();
            lessonRepository.save(todayLesson);

            attendanceRepository.save(Attendance.builder()
                    .lesson(todayLesson)
                    .child(testChild)
                    .build());

            mockMvc.perform(get("/api/lessons/today")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.topic=='Занятие сегодня')]").exists())
                    .andExpect(jsonPath("$[0].students", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Родитель получает 403")
        void getToday_AsParent_Returns403() throws Exception {
            mockMvc.perform(get("/api/lessons/today")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/lessons/{id}/attendance")
    class MarkAttendance {

        @Test
        @DisplayName("Учитель отмечает посещаемость")
        void markAttendance_Success() throws Exception {
            AttendanceRequest request = new AttendanceRequest();
            request.setChildId(testChild.getId());
            request.setPresent(true);

            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/attendance")
                            .header("Authorization", "Bearer " + teacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isOk());

            // Проверяем, что посещаемость обновилась
            Attendance attendance = attendanceRepository
                    .findByLessonIdAndChildId(testLesson.getId(), testChild.getId())
                    .orElseThrow();
            assertTrue(attendance.getPresent());
        }

        @Test
        @DisplayName("Отметка несуществующей записи возвращает 404")
        void markAttendance_NotFound_Returns404() throws Exception {
            AttendanceRequest request = new AttendanceRequest();
            request.setChildId(UUID.randomUUID());
            request.setPresent(true);

            mockMvc.perform(post("/api/lessons/" + testLesson.getId() + "/attendance")
                            .header("Authorization", "Bearer " + teacherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isNotFound());
        }
    }
}

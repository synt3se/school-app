package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.entity.Notification;
import com.github.synt3se.entity.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/notifications")
    class GetNotifications {

        @Test
        @DisplayName("Получение всех уведомлений")
        void getNotifications_ReturnsAll() throws Exception {
            // Создаём уведомления
            createNotification("Заголовок 1", "Сообщение 1", false);
            createNotification("Заголовок 2", "Сообщение 2", true);

            mockMvc.perform(get("/api/notifications")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        @DisplayName("Получение только непрочитанных")
        void getNotifications_UnreadOnly() throws Exception {
            createNotification("Непрочитанное", "Сообщение", false);
            createNotification("Прочитанное", "Сообщение", true);

            mockMvc.perform(get("/api/notifications")
                            .param("unreadOnly", "true")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("Непрочитанное"));
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/unread/count")
    class GetUnreadCount {

        @Test
        @DisplayName("Подсчёт непрочитанных уведомлений")
        void getUnreadCount_Success() throws Exception {
            createNotification("Непрочитанное 1", "Сообщение", false);
            createNotification("Непрочитанное 2", "Сообщение", false);
            createNotification("Прочитанное", "Сообщение", true);

            mockMvc.perform(get("/api/notifications/unread/count")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(2));
        }
    }

    @Nested
    @DisplayName("POST /api/notifications/{id}/read")
    class MarkAsRead {

        @Test
        @DisplayName("Отметка уведомления как прочитанного")
        void markAsRead_Success() throws Exception {
            Notification notification = createNotification("Тест", "Сообщение", false);

            mockMvc.perform(post("/api/notifications/" + notification.getId() + "/read")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk());

            // После @Modifying запроса нужно проверить через count
            long unreadCount = notificationRepository.countByUserIdAndRead(parentUser.getId(), false);
            assertEquals(0, unreadCount);
        }
    }

    @Nested
    @DisplayName("POST /api/notifications/read-all")
    class MarkAllAsRead {

        @Test
        @DisplayName("Отметка всех уведомлений как прочитанных")
        void markAllAsRead_Success() throws Exception {
            createNotification("Уведомление 1", "Сообщение", false);
            createNotification("Уведомление 2", "Сообщение", false);

            mockMvc.perform(post("/api/notifications/read-all")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk());

            // Проверяем, что все уведомления прочитаны
            long unreadCount = notificationRepository.countByUserIdAndRead(parentUser.getId(), false);
            assertEquals(0, unreadCount);
        }
    }

    private Notification createNotification(String title, String message, boolean read) {
        Notification notification = Notification.builder()
                .user(parentUser)
                .type(NotificationType.LESSON_REMINDER)
                .title(title)
                .message(message)
                .read(read)
                .build();
        return notificationRepository.save(notification);
    }
}
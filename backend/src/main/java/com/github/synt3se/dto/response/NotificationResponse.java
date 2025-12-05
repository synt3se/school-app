package com.github.synt3se.dto.response;

import com.github.synt3se.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean read;
    private UUID lessonId;
    private UUID gradeId;
    private LocalDateTime createdAt;
}

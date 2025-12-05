package com.github.synt3se.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RescheduleRequest {

    @NotNull(message = "ID исходного занятия обязателен")
    private UUID fromLessonId;

    @NotNull(message = "ID целевого занятия обязателен")
    private UUID toLessonId;
}

package com.github.synt3se.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RestoreRequest {

    @NotNull(message = "ID пропущенного занятия обязателен")
    private UUID missedLessonId;

    @NotNull(message = "ID целевого занятия обязателен")
    private UUID targetLessonId;
}

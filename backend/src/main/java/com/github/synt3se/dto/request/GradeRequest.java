package com.github.synt3se.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class GradeRequest {

    @NotNull(message = "ID ребёнка обязателен")
    private UUID childId;

    @NotNull(message = "Оценка обязательна")
    @Min(value = 1, message = "Оценка должна быть от 1 до 5")
    @Max(value = 5, message = "Оценка должна быть от 1 до 5")
    private Integer value;

    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;
}


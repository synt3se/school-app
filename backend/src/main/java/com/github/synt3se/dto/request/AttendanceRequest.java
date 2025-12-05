package com.github.synt3se.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceRequest {

    @NotNull(message = "ID ребёнка обязателен")
    private UUID childId;

    @NotNull(message = "Статус присутствия обязателен")
    private Boolean present;
}

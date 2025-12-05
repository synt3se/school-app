package com.github.synt3se.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateChildRequest {

    @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
    private String fullName;

    private LocalDate birthDate;
}

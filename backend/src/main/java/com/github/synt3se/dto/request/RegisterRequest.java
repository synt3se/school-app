package com.github.synt3se.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegisterRequest {

    @NotBlank(message = "ФИО обязательно")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    @NotNull(message = "Филиал обязателен")
    private UUID branchId;

    @NotNull(message = "Данные ребёнка обязательны")
    @Valid
    private ChildData child;

    @Data
    public static class ChildData {
        @NotBlank(message = "ФИО ребёнка обязательно")
        @Size(min = 2, max = 100)
        private String fullName;

        @NotNull(message = "Дата рождения обязательна")
        private LocalDate birthDate;
    }
}

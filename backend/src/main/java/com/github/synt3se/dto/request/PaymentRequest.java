package com.github.synt3se.dto.request;

import com.github.synt3se.entity.PaymentPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {

    @NotNull(message = "ID курса обязателен")
    private UUID courseId;

    @NotNull(message = "Период оплаты обязателен")
    private PaymentPeriod period;
}

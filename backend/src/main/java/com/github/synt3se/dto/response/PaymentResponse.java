package com.github.synt3se.dto.response;

import com.github.synt3se.entity.PaymentPeriod;
import com.github.synt3se.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private String course;
    private PaymentPeriod period;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}

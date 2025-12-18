package com.github.synt3se.controller;

import com.github.synt3se.BaseIntegrationTest;
import com.github.synt3se.dto.request.PaymentRequest;
import com.github.synt3se.entity.Payment;
import com.github.synt3se.entity.PaymentPeriod;
import com.github.synt3se.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/payments")
    class GetPayments {

        @Test
        @DisplayName("Родитель получает историю платежей")
        void getPayments_Success() throws Exception {
            // Создаём платёж
            Payment payment = Payment.builder()
                    .user(parentUser)
                    .course(testCourse)
                    .amount(new BigDecimal("8000"))
                    .period(PaymentPeriod.MONTH)
                    .status(PaymentStatus.PAID)
                    .build();
            paymentRepository.save(payment);

            mockMvc.perform(get("/api/payments")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].amount").value(8000))
                    .andExpect(jsonPath("$.content[0].period").value("MONTH"));
        }

        @Test
        @DisplayName("Учитель получает 403")
        void getPayments_AsTeacher_Returns403() throws Exception {
            mockMvc.perform(get("/api/payments")
                            .header("Authorization", "Bearer " + teacherToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/payments")
    class CreatePayment {

        @Test
        @DisplayName("Создание платежа за урок")
        void createPayment_PerLesson_Success() throws Exception {
            PaymentRequest request = new PaymentRequest();
            request.setCourseId(testCourse.getId());
            request.setPeriod(PaymentPeriod.LESSON);

            mockMvc.perform(post("/api/payments")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.amount").value(1000))
                    .andExpect(jsonPath("$.period").value("LESSON"));
        }

        @Test
        @DisplayName("Создание платежа за месяц")
        void createPayment_PerMonth_Success() throws Exception {
            PaymentRequest request = new PaymentRequest();
            request.setCourseId(testCourse.getId());
            request.setPeriod(PaymentPeriod.MONTH);

            mockMvc.perform(post("/api/payments")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.amount").value(8000));
        }

        @Test
        @DisplayName("Создание платежа за год")
        void createPayment_PerYear_Success() throws Exception {
            PaymentRequest request = new PaymentRequest();
            request.setCourseId(testCourse.getId());
            request.setPeriod(PaymentPeriod.YEAR);

            mockMvc.perform(post("/api/payments")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.amount").value(80000));
        }

        @Test
        @DisplayName("Платёж с несуществующим курсом возвращает 404")
        void createPayment_NonExistentCourse_Returns404() throws Exception {
            PaymentRequest request = new PaymentRequest();
            request.setCourseId(UUID.randomUUID());
            request.setPeriod(PaymentPeriod.MONTH);

            mockMvc.perform(post("/api/payments")
                            .header("Authorization", "Bearer " + parentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/payments/prices")
    class GetPrices {

        @Test
        @DisplayName("Родитель получает тарифы филиала")
        void getPrices_Success() throws Exception {
            mockMvc.perform(get("/api/payments/prices")
                            .header("Authorization", "Bearer " + parentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lesson").value(1000))
                    .andExpect(jsonPath("$.month").value(8000))
                    .andExpect(jsonPath("$.year").value(80000));
        }
    }
}
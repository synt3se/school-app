package com.github.synt3se.service;

import com.github.synt3se.dto.request.PaymentRequest;
import com.github.synt3se.dto.response.PaymentResponse;
import com.github.synt3se.dto.response.PricesResponse;
import com.github.synt3se.entity.*;
import com.github.synt3se.exception.BadRequestException;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public Page<PaymentResponse> getHistory(UUID userId, Pageable pageable) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public PaymentResponse create(UUID userId, PaymentRequest request) {
        User user = userRepository.findByIdWithChild(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Курс не найден"));

        Branch branch = user.getBranch();
        if (branch == null) {
            throw new BadRequestException("Пользователь не привязан к филиалу");
        }

        BigDecimal amount = calculateAmount(branch, request.getPeriod());

        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .period(request.getPeriod())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // TODO: already PAID for demo
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return toResponse(payment);
    }

    public PricesResponse getPrices(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Branch branch = user.getBranch();
        if (branch == null) {
            throw new BadRequestException("Пользователь не привязан к филиалу");
        }

        return PricesResponse.builder()
                .lesson(branch.getPricePerLesson())
                .month(branch.getPricePerMonth())
                .year(branch.getPricePerYear())
                .build();
    }

    private BigDecimal calculateAmount(Branch branch, PaymentPeriod period) {
        BigDecimal amount = switch (period) {
            case LESSON -> branch.getPricePerLesson();
            case MONTH -> branch.getPricePerMonth();
            case YEAR -> branch.getPricePerYear();
        };

        if (amount == null) {
            throw new BadRequestException("Тариф не установлен для периода: " + period);
        }

        return amount;
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .course(payment.getCourse() != null ? payment.getCourse().getName() : null)
                .period(payment.getPeriod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

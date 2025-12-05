package com.github.synt3se.controller;


import com.github.synt3se.dto.request.PaymentRequest;
import com.github.synt3se.dto.response.PaymentResponse;
import com.github.synt3se.dto.response.PricesResponse;
import com.github.synt3se.entity.User;
import com.github.synt3se.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PARENT')")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.getHistory(user.getId(), PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(user.getId(), request));
    }

    @GetMapping("/prices")
    public ResponseEntity<PricesResponse> getPrices(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPrices(user.getId()));
    }
}

package com.github.synt3se.repository;

import com.github.synt3se.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}

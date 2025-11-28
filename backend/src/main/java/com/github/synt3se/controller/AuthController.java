package com.github.synt3se.controller;

import com.github.synt3se.dto.request.LoginRequest;
import com.github.synt3se.dto.request.RegisterRequest;
import com.github.synt3se.dto.response.AuthResponse;
import com.github.synt3se.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    // JwtTokenProvider
    // AuthManager

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new AuthResponse());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, UUID>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", UUID.randomUUID()));
    }

}

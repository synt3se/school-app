package com.github.synt3se.service;

import com.github.synt3se.dto.request.LoginRequest;
import com.github.synt3se.dto.request.RegisterRequest;
import com.github.synt3se.dto.response.*;
import com.github.synt3se.entity.*;
import com.github.synt3se.exception.BadRequestException;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.*;
import com.github.synt3se.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailWithChild(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Неверный email или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Неверный email или пароль");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .user(toUserResponse(user))
                .build();
    }

    @Transactional
    public UUID register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new NotFoundException("Филиал не найден"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PARENT)
                .branch(branch)
                .build();

        Child child = Child.builder()
                .fullName(request.getChild().getFullName())
                .birthDate(request.getChild().getBirthDate())
                .parent(user)
                .branch(branch)
                .build();

        user.setChild(child);
        userRepository.save(user);

        return user.getId();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .branch(user.getBranch() != null ? toBranchResponse(user.getBranch()) : null)
                .child(user.getChild() != null ? toChildResponse(user.getChild()) : null)
                .build();
    }

    public ChildResponse toChildResponse(Child child) {
        return ChildResponse.builder()
                .id(child.getId())
                .fullName(child.getFullName())
                .birthDate(child.getBirthDate())
                .age(child.getAge())
                .courses(child.getCourses() != null
                        ? child.getCourses().stream()
                        .map(c -> CourseResponse.builder().id(c.getId()).name(c.getName()).build())
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    private BranchResponse toBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .build();
    }
}

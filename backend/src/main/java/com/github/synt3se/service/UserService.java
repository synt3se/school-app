package com.github.synt3se.service;

import com.github.synt3se.dto.request.UpdateUserRequest;
import com.github.synt3se.dto.response.UserResponse;
import com.github.synt3se.entity.User;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findByIdWithChild(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return authService.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findByIdWithChild(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);
        return authService.toUserResponse(user);
    }
}

package com.github.synt3se.service;

import com.github.synt3se.dto.request.UpdateChildRequest;
import com.github.synt3se.dto.response.ChildResponse;
import com.github.synt3se.entity.Child;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final AuthService authService;

    public ChildResponse getChild(UUID parentId) {
        Child child = childRepository.findByParentIdWithCourses(parentId)
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));
        return authService.toChildResponse(child);
    }

    @Transactional
    public ChildResponse updateChild(UUID parentId, UpdateChildRequest request) {
        Child child = childRepository.findByParentIdWithCourses(parentId)
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));

        if (StringUtils.hasText(request.getFullName())) {
            child.setFullName(request.getFullName());
        }
        if (request.getBirthDate() != null) {
            child.setBirthDate(request.getBirthDate());
        }

        childRepository.save(child);
        return authService.toChildResponse(child);
    }
}

package com.github.synt3se.controller;

import com.github.synt3se.dto.request.GradeRequest;
import com.github.synt3se.dto.response.GradeResponse;
import com.github.synt3se.entity.User;
import com.github.synt3se.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<GradeResponse>> getGrades(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gradeService.getGradesForChild(user.getId()));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<GradeResponse>> getGradesByCourse(
            @AuthenticationPrincipal User user,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(gradeService.getGradesForChildByCourse(user.getId(), courseId));
    }

    @PostMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<GradeResponse> addGrade(
            @AuthenticationPrincipal User user,
            @PathVariable UUID lessonId,
            @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gradeService.addGrade(user.getId(), lessonId, request));
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<GradeResponse>> getGradesForLesson(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(gradeService.getGradesForLesson(lessonId));
    }
}

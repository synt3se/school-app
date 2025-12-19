package com.github.synt3se.controller;

import com.github.synt3se.dto.request.AttendanceRequest;
import com.github.synt3se.dto.request.RescheduleRequest;
import com.github.synt3se.dto.request.RestoreRequest;
import com.github.synt3se.dto.response.LessonResponse;
import com.github.synt3se.dto.response.LessonWithAttendanceResponse;
import com.github.synt3se.entity.User;
import com.github.synt3se.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<LessonResponse>> getUpcoming(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(lessonService.getUpcoming(user.getId(), limit));
    }

    @GetMapping("/week")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<LessonResponse>> getWeekSchedule(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lessonService.getWeekSchedule(user.getId()));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<LessonResponse>> getAvailable(
            @AuthenticationPrincipal User user,
            @RequestParam UUID courseId) {
        return ResponseEntity.ok(lessonService.getAvailableForReschedule(user.getId(), courseId));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<LessonResponse> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(lessonService.cancel(user.getId(), id));
    }

    @PostMapping("/reschedule")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<LessonResponse> reschedule(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(lessonService.reschedule(user.getId(), request));
    }

    @PostMapping("/restore")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<LessonResponse> restore(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RestoreRequest request) {
        return ResponseEntity.ok(lessonService.restore(user.getId(), request));
    }

    @GetMapping("/teacher/upcoming")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<LessonWithAttendanceResponse>> getUpcomingForTeacher(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(lessonService.getUpcomingForTeacher(user.getId(), limit));
    }

    @GetMapping("/teacher/today")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<LessonWithAttendanceResponse>> getWeekForTeacher(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lessonService.getTodayForTeacher(user.getId()));
    }

    @PostMapping("/{id}/attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> markAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request) {
        lessonService.markAttendance(id, request);
        return ResponseEntity.ok().build();
    }
}

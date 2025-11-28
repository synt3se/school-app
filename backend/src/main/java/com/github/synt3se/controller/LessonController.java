package com.github.synt3se.controller;

import com.github.synt3se.dto.request.AttendaceRequest;
import com.github.synt3se.dto.response.AttendanceResponse;
import com.github.synt3se.dto.response.LessonResponse;
import com.github.synt3se.service.LessonService;
import com.github.synt3se.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final UserService userService;

    @GetMapping("/upcoming")
    public ResponseEntity<List<LessonResponse>> getUpcomingLessons(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/week")
    public ResponseEntity<List<LessonResponse>> getWeekLessons(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/today")
    public ResponseEntity<List<LessonResponse>> getTodayLessons(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/branch/{branchid}")
    public ResponseEntity<List<LessonResponse>> getBranchLessons(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PatchMapping("/{id}/attendance")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendaceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new AttendanceResponse());
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<LessonResponse> rescheduleLesson(
            @PathVariable UUID id,
            @Valid @RequestBody AttendaceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new LessonResponse());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LessonResponse> cancelLesson(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new LessonResponse());
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<LessonResponse> restoreLesson(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new LessonResponse());
    }

}

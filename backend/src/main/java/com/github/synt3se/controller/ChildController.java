package com.github.synt3se.controller;

import com.github.synt3se.dto.request.UpdateChildRequest;
import com.github.synt3se.dto.response.ChildResponse;
import com.github.synt3se.entity.User;
import com.github.synt3se.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PARENT')")
public class ChildController {

    private final ChildService childService;

    @GetMapping
    public ResponseEntity<ChildResponse> getChild(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(childService.getChild(user.getId()));
    }

    @PutMapping
    public ResponseEntity<ChildResponse> updateChild(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateChildRequest request) {
        return ResponseEntity.ok(childService.updateChild(user.getId(), request));
    }
}

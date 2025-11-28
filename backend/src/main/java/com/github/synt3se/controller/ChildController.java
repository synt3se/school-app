package com.github.synt3se.controller;

import com.github.synt3se.dto.request.ChildRequest;
import com.github.synt3se.dto.response.BonusJournalResponse;
import com.github.synt3se.dto.response.ChildResponse;
import com.github.synt3se.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/children/{id}")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @GetMapping
    public ResponseEntity<ChildResponse> getChild(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new ChildResponse());
    }

    @PutMapping
    public ResponseEntity<ChildResponse> updateChild(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChildRequest request) {
        return ResponseEntity.ok(new ChildResponse());
    }

    @GetMapping("/bonus-journal")
    public ResponseEntity<List<BonusJournalResponse>> getBonusJournal(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new ArrayList<>());
    }


}

package com.github.synt3se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildResponse {

    private UUID id;
    private String fullName;
    private LocalDate birthDate;
    private int age;
    private List<CourseResponse> courses;
}

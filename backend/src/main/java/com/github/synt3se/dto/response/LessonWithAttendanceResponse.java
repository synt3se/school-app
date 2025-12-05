package com.github.synt3se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonWithAttendanceResponse {

    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CourseResponse course;
    private String topic;
    private List<StudentAttendanceResponse> students;
}

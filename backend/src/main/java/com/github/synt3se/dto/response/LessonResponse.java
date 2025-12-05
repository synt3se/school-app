package com.github.synt3se.dto.response;

import com.github.synt3se.entity.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CourseResponse course;
    private BranchResponse branch;
    private String teacher;
    private LessonStatus status;
    private String topic;
}

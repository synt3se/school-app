package com.github.synt3se.dto.response;

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
public class GradeResponse {

    private UUID id;
    private Integer value;
    private String comment;
    private String course;
    private String lessonTopic;
    private LocalDateTime lessonDate;
    private String teacher;
    private LocalDateTime createdAt;
}
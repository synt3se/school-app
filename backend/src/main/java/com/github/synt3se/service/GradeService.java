package com.github.synt3se.service;

import com.github.synt3se.dto.request.GradeRequest;
import com.github.synt3se.dto.response.GradeResponse;
import com.github.synt3se.entity.*;
import com.github.synt3se.exception.BadRequestException;
import com.github.synt3se.exception.ConflictException;
import com.github.synt3se.exception.ForbiddenException;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final LessonRepository lessonRepository;
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<GradeResponse> getGradesForChild(UUID parentId) {
        Child child = childRepository.findByParentIdWithCourses(parentId)
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));

        return gradeRepository.findByChildIdWithDetails(child.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<GradeResponse> getGradesForChildByCourse(UUID parentId, UUID courseId) {
        Child child = childRepository.findByParentIdWithCourses(parentId)
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));

        return gradeRepository.findByChildIdAndCourseId(child.getId(), courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeResponse addGrade(UUID teacherId, UUID lessonId, GradeRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Учитель не найден"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Занятие не найдено"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw ForbiddenException.notYourLesson();
        }

        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));

        if (gradeRepository.existsByLessonIdAndChildId(lessonId, request.getChildId())) {
            throw ConflictException.alreadyGraded();
        }

        Grade grade = Grade.builder()
                .child(child)
                .lesson(lesson)
                .teacher(teacher)
                .value(request.getValue())
                .comment(request.getComment())
                .build();

        gradeRepository.save(grade);

        notificationService.notifyGradeAdded(child, grade);

        return toResponse(grade);
    }

    public List<GradeResponse> getGradesForLesson(UUID lessonId) {
        return gradeRepository.findByLessonIdWithChild(lessonId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private GradeResponse toResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .value(grade.getValue())
                .comment(grade.getComment())
                .course(grade.getLesson().getCourse().getName())
                .lessonTopic(grade.getLesson().getTopic())
                .lessonDate(grade.getLesson().getStartTime())
                .teacher(grade.getTeacher().getFullName())
                .createdAt(grade.getCreatedAt())
                .build();
    }
}

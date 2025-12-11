package com.github.synt3se.service;

import com.github.synt3se.dto.request.AttendanceRequest;
import com.github.synt3se.dto.request.RescheduleRequest;
import com.github.synt3se.dto.request.RestoreRequest;
import com.github.synt3se.dto.response.*;
import com.github.synt3se.entity.*;
import com.github.synt3se.exception.BadRequestException;
import com.github.synt3se.exception.ConflictException;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;
    private final ChildRepository childRepository;

    public List<LessonResponse> getUpcoming(UUID parentId, int limit) {
        Child child = getChildByParent(parentId);
        List<Lesson> lessons = lessonRepository.findUpcomingByChildId(
                child.getId(),
                LocalDateTime.now(),
                LessonStatus.SCHEDULED,
                limit
        );
        return lessons.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<LessonResponse> getWeekSchedule(UUID parentId) {
        Child child = getChildByParent(parentId);
        LocalDate today = LocalDate.now();
        LocalDateTime weekStart = today.atStartOfDay();
        LocalDateTime weekEnd = today.plusDays(7).atStartOfDay();

        List<Lesson> lessons = lessonRepository.findByChildIdAndTimeRange(
                child.getId(), weekStart, weekEnd
        );
        return lessons.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<LessonResponse> getAvailableForReschedule(UUID parentId, UUID courseId) {
        Child child = getChildByParent(parentId);
        List<Lesson> lessons = lessonRepository.findAvailableForReschedule(
                courseId,
                child.getBranch().getId(),
                child.getId(),
                LocalDateTime.now()
        );
        return lessons.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse cancel(UUID parentId, UUID lessonId) {
        Child child = getChildByParent(parentId);
        Lesson lesson = getLessonById(lessonId);

        Attendance attendance = attendanceRepository.findByLessonIdAndChildId(lessonId, child.getId())
                .orElseThrow(() -> new BadRequestException("Ребёнок не записан на это занятие"));

        if (attendance.getPresent() != null) {
            throw ConflictException.alreadyMarked();
        }

        attendance.setPresent(false);
        attendanceRepository.save(attendance);

        return toResponse(lesson);
    }

    @Transactional
    public LessonResponse reschedule(UUID parentId, RescheduleRequest request) {
        Child child = getChildByParent(parentId);
        Lesson fromLesson = getLessonById(request.getFromLessonId());
        Lesson toLesson = getLessonById(request.getToLessonId());

        validateSameCourseAndBranch(fromLesson, toLesson);

        Attendance fromAttendance = attendanceRepository.findByLessonIdAndChildId(
                        request.getFromLessonId(), child.getId())
                .orElseThrow(() -> new BadRequestException("Ребёнок не записан на исходное занятие"));

        if (attendanceRepository.findByLessonIdAndChildId(request.getToLessonId(), child.getId()).isPresent()) {
            throw ConflictException.alreadyEnrolled();
        }

        fromAttendance.setPresent(false);
        fromAttendance.setRescheduledTo(toLesson);
        attendanceRepository.save(fromAttendance);

        Attendance toAttendance = Attendance.builder()
                .lesson(toLesson)
                .child(child)
                .build();
        attendanceRepository.save(toAttendance);

        return toResponse(toLesson);
    }

    @Transactional
    public LessonResponse restore(UUID parentId, RestoreRequest request) {
        Child child = getChildByParent(parentId);
        Lesson missedLesson = getLessonById(request.getMissedLessonId());
        Lesson targetLesson = getLessonById(request.getTargetLessonId());

        validateSameCourseAndBranch(missedLesson, targetLesson);

        Attendance missedAttendance = attendanceRepository.findByLessonIdAndChildId(
                        request.getMissedLessonId(), child.getId())
                .orElseThrow(() -> new BadRequestException("Запись о занятии не найдена"));

        if (missedAttendance.getPresent() != null && missedAttendance.getPresent()) {
            throw new BadRequestException("Занятие не было пропущено — нельзя восстановить");
        }

        if (attendanceRepository.findByLessonIdAndChildId(request.getTargetLessonId(), child.getId()).isPresent()) {
            throw ConflictException.alreadyEnrolled();
        }

        missedAttendance.setRescheduledTo(targetLesson);
        attendanceRepository.save(missedAttendance);

        Attendance targetAttendance = Attendance.builder()
                .lesson(targetLesson)
                .child(child)
                .build();
        attendanceRepository.save(targetAttendance);

        return toResponse(targetLesson);
    }

    public List<LessonWithAttendanceResponse> getTodayForTeacher(UUID teacherId) {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);

        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStartTimeBetweenOrderByStartTime(
                teacherId, dayStart, dayEnd
        );

        return lessons.stream().map(lesson -> {
            List<Attendance> attendances = attendanceRepository.findByLessonIdWithChild(lesson.getId());
            return toLessonWithAttendance(lesson, attendances);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void markAttendance(UUID lessonId, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findByLessonIdAndChildId(lessonId, request.getChildId())
                .orElseThrow(() -> new NotFoundException("Запись посещаемости не найдена"));

        attendance.setPresent(request.getPresent());
        attendanceRepository.save(attendance);
    }

    private Child getChildByParent(UUID parentId) {
        return childRepository.findByParentIdWithCourses(parentId)
                .orElseThrow(() -> new NotFoundException("Ребёнок не найден"));
    }

    private Lesson getLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Занятие не найдено"));
    }

    private void validateSameCourseAndBranch(Lesson from, Lesson to) {
        if (!from.getCourse().getId().equals(to.getCourse().getId())) {
            throw new BadRequestException("Нельзя перенести занятие на другой курс");
        }
        if (!from.getBranch().getId().equals(to.getBranch().getId())) {
            throw new BadRequestException("Нельзя перенести занятие в другой филиал");
        }
    }

    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .course(CourseResponse.builder()
                        .id(lesson.getCourse().getId())
                        .name(lesson.getCourse().getName())
                        .build())
                .branch(BranchResponse.builder()
                        .id(lesson.getBranch().getId())
                        .name(lesson.getBranch().getName())
                        .address(lesson.getBranch().getAddress())
                        .build())
                .teacher(lesson.getTeacher() != null ? lesson.getTeacher().getFullName() : null)
                .status(lesson.getStatus())
                .topic(lesson.getTopic())
                .build();
    }

    private LessonWithAttendanceResponse toLessonWithAttendance(Lesson lesson, List<Attendance> attendances) {
        return LessonWithAttendanceResponse.builder()
                .id(lesson.getId())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .course(CourseResponse.builder()
                        .id(lesson.getCourse().getId())
                        .name(lesson.getCourse().getName())
                        .build())
                .topic(lesson.getTopic())
                .students(attendances.stream()
                        .map(a -> StudentAttendanceResponse.builder()
                                .childId(a.getChild().getId())
                                .childName(a.getChild().getFullName())
                                .present(a.getPresent())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

package com.github.synt3se.config;

import com.github.synt3se.entity.*;
import com.github.synt3se.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    // ID из тестовых данных Liquibase
    private static final UUID BRANCH_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TEACHER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID COURSE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID CHILD_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing dynamic test data...");

        // Проверяем, есть ли уже занятия на сегодня
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        var existingLessons = lessonRepository.findByTeacherIdAndStartTimeBetweenOrderByStartTime(
                TEACHER_ID, todayStart, todayEnd);

        if (!existingLessons.isEmpty()) {
            log.info("Lessons for today already exist, skipping...");
            return;
        }

        // Загружаем связанные сущности
        var branch = branchRepository.findById(BRANCH_ID).orElse(null);
        var teacher = userRepository.findById(TEACHER_ID).orElse(null);
        var course = courseRepository.findById(COURSE_ID).orElse(null);
        var child = childRepository.findById(CHILD_ID).orElse(null);

        if (branch == null || teacher == null || course == null || child == null) {
            log.warn("Base test data not found, skipping dynamic initialization");
            return;
        }

        // Создаём занятие на сегодня (10:00 - 11:30)
        Lesson todayLesson = Lesson.builder()
                .startTime(today.atTime(10, 0))
                .endTime(today.atTime(11, 30))
                .course(course)
                .branch(branch)
                .teacher(teacher)
                .status(LessonStatus.SCHEDULED)
                .topic("Занятие на сегодня - Натюрморт")
                .build();
        lessonRepository.save(todayLesson);

        // Записываем ребёнка на занятие
        Attendance todayAttendance = Attendance.builder()
                .lesson(todayLesson)
                .child(child)
                .build();
        attendanceRepository.save(todayAttendance);

        // Создаём занятие на завтра (10:00 - 11:30)
        Lesson tomorrowLesson = Lesson.builder()
                .startTime(today.plusDays(1).atTime(10, 0))
                .endTime(today.plusDays(1).atTime(11, 30))
                .course(course)
                .branch(branch)
                .teacher(teacher)
                .status(LessonStatus.SCHEDULED)
                .topic("Занятие на завтра - Портрет")
                .build();
        lessonRepository.save(tomorrowLesson);

        Attendance tomorrowAttendance = Attendance.builder()
                .lesson(tomorrowLesson)
                .child(child)
                .build();
        attendanceRepository.save(tomorrowAttendance);

        // Создаём занятие на послезавтра (для переноса)
        Lesson dayAfterLesson = Lesson.builder()
                .startTime(today.plusDays(2).atTime(14, 0))
                .endTime(today.plusDays(2).atTime(15, 30))
                .course(course)
                .branch(branch)
                .teacher(teacher)
                .status(LessonStatus.SCHEDULED)
                .topic("Дополнительное занятие - Пейзаж")
                .build();
        lessonRepository.save(dayAfterLesson);
        // Ребёнок НЕ записан — можно использовать для переноса

        log.info("Created dynamic lessons: today={}, tomorrow={}, dayAfter={}",
                todayLesson.getId(), tomorrowLesson.getId(), dayAfterLesson.getId());
    }
}


package com.github.synt3se.repository;

import com.github.synt3se.entity.Lesson;
import com.github.synt3se.entity.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @Query("""
        SELECT l FROM Lesson l 
        JOIN Attendance a ON a.lesson = l 
        WHERE a.child.id = :childId 
          AND l.startTime >= :from 
          AND l.startTime < :to
          AND (a.present IS NULL OR a.present = true)
          AND a.rescheduledTo IS NULL
        ORDER BY l.startTime
        """)
    List<Lesson> findByChildIdAndTimeRange(UUID childId, LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT l FROM Lesson l 
        JOIN Attendance a ON a.lesson = l 
        WHERE a.child.id = :childId 
          AND l.startTime >= :from
          AND l.status = :status
          AND a.present IS NULL
          AND a.rescheduledTo IS NULL
        ORDER BY l.startTime
        LIMIT :limit
        """)
    List<Lesson> findUpcomingByChildId(UUID childId, LocalDateTime from, LessonStatus status, int limit);

    @Query("""
        SELECT l FROM Lesson l 
        WHERE l.course.id = :courseId 
          AND l.branch.id = :branchId
          AND l.startTime > :from
          AND l.status = 'SCHEDULED'
          AND NOT EXISTS (
              SELECT 1 FROM Attendance a 
              WHERE a.lesson = l AND a.child.id = :childId
          )
        ORDER BY l.startTime
        """)
    List<Lesson> findAvailableForReschedule(UUID courseId, UUID branchId, UUID childId, LocalDateTime from);

    List<Lesson> findByTeacherIdAndStartTimeBetweenOrderByStartTime(
            UUID teacherId, LocalDateTime from, LocalDateTime to);
}

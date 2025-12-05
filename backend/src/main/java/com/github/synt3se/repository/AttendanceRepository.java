package com.github.synt3se.repository;

import com.github.synt3se.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByLessonIdAndChildId(UUID lessonId, UUID childId);

    List<Attendance> findByLessonId(UUID lessonId);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.child WHERE a.lesson.id = :lessonId")
    List<Attendance> findByLessonIdWithChild(UUID lessonId);
}

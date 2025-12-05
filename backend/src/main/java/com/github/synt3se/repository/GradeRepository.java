package com.github.synt3se.repository;

import com.github.synt3se.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

    @Query("SELECT g FROM Grade g " +
            "JOIN FETCH g.lesson l " +
            "JOIN FETCH l.course " +
            "JOIN FETCH g.teacher " +
            "WHERE g.child.id = :childId " +
            "ORDER BY g.createdAt DESC")
    List<Grade> findByChildIdWithDetails(@Param("childId") UUID childId);

    @Query("SELECT g FROM Grade g " +
            "JOIN FETCH g.child " +
            "WHERE g.lesson.id = :lessonId")
    List<Grade> findByLessonIdWithChild(@Param("lessonId") UUID lessonId);

    @Query("SELECT g FROM Grade g " +
            "JOIN FETCH g.lesson l " +
            "JOIN FETCH l.course c " +
            "WHERE g.child.id = :childId AND c.id = :courseId " +
            "ORDER BY g.createdAt DESC")
    List<Grade> findByChildIdAndCourseId(@Param("childId") UUID childId, @Param("courseId") UUID courseId);

    boolean existsByLessonIdAndChildId(UUID lessonId, UUID childId);
}

package com.github.synt3se.repository;

import com.github.synt3se.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findByBranchIdAndActiveTrue(UUID branchId);
}

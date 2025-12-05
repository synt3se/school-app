package com.github.synt3se.repository;

import com.github.synt3se.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ChildRepository extends JpaRepository<Child, UUID> {

    Optional<Child> findByParentId(UUID parentId);

    @Query("SELECT c FROM Child c LEFT JOIN FETCH c.courses WHERE c.parent.id = :parentId")
    Optional<Child> findByParentIdWithCourses(UUID parentId);
}


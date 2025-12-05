package com.github.synt3se.repository;

import com.github.synt3se.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.child LEFT JOIN FETCH u.branch WHERE u.id = :id")
    Optional<User> findByIdWithChild(UUID id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.child LEFT JOIN FETCH u.branch WHERE u.email = :email")
    Optional<User> findByEmailWithChild(String email);
}

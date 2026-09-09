package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface    UserRepository extends JpaRepository<User, Integer> {
    java.util.List<User> findByRole(UserRole role);
    Page<User> findByRole(UserRole role, Pageable pageable);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}

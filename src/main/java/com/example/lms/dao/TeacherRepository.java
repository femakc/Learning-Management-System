package com.example.lms.dao;

import com.example.lms.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByExternalId(UUID externalId);
}

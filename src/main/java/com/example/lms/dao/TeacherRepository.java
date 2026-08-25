package com.example.lms.dao;

import com.example.lms.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByExternalId(UUID externalId);

    @Query(value = "SELECT * FROM teachers WHERE external_id = :externalId", nativeQuery = true)
    Optional<Teacher> findByExternalIdAny(@Param("externalId") UUID externalId);
}

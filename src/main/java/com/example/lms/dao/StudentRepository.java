package com.example.lms.dao;

import com.example.lms.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByExternalId(UUID id);
    Set<Student> findByExternalIdIn(Set<UUID> externalIds);

    @Query(value = "SELECT * FROM students WHERE external_id = :externalId",  nativeQuery = true)
    Optional<Student> findByExternalIdAny(UUID externalId);

    @Query(value = "SELECT COUNT(*) > 0 " +
            "FROM students " +
            "WHERE name = :name " +
            "AND external_id != :externalId",
            nativeQuery = true)
    boolean existsByNameAny(@Param("name") String name, @Param("externalId") UUID externalId);
}

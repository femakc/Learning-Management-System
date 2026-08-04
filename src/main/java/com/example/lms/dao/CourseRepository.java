package com.example.lms.dao;

import com.example.lms.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByExternalId (UUID externalId);
    Set<Course> findByExternalIdIn (Set<UUID> externalIds);

    @Query(value = "SELECT * FROM courses WHERE external_id = :externalId", nativeQuery = true)
    Optional<Course> findAnyByExternalId(@Param("externalId") UUID externalId);

    @Query(value = "SELECT COUNT(*) > 0 FROM courses WHERE name = :name", nativeQuery = true)
    boolean existsByNameAny(@Param("name") String name);

    @Query(value = "SELECT * FROM courses WHERE name = :name", nativeQuery = true)
    Optional<Course> findByNameAny(@Param("name") String name);
}

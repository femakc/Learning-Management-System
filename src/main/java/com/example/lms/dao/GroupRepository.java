package com.example.lms.dao;

import com.example.lms.models.Course;
import com.example.lms.models.Group;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByExternalId(UUID id);
    Set<Group> findAllByExternalIdIn(@NotEmpty(
            message = "у студента должна быть хотя бы одна группа") Set<UUID> uuids
    );
    @Query(value = "SELECT * FROM groups WHERE external_id = :externalId", nativeQuery = true)
    Optional<Group> findAnyByExternalId(@Param("externalId") UUID externalId);

    @Query(value = "SELECT * FROM groups WHERE name = :name", nativeQuery = true)
    Optional<Group> findByNameAny(@Param("name") String name);

    @Query(value = "SELECT COUNT(*) > 0 " +
            "FROM groups " +
            "WHERE name = :name " +
            "AND external_id != :externalId",
            nativeQuery = true)
    boolean existsByNameAny(@Param("name") String name, @Param("externalId") UUID externalId);
}

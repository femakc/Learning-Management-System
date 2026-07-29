package com.example.lms.dao;

import com.example.lms.models.Group;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByExternalId(UUID id);
    Set<Group> findAllByExternalIdIn(@NotEmpty(
            message = "у студента должна быть хотя бы одна группа") Set<UUID> uuids
    );
}

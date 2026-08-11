package com.example.lms.dao;

import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.models.Course;
import com.example.lms.models.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByExternalId(UUID externalId);

    Optional<Schedule> findByGroupId(Long groupId);
    Optional<Schedule> findByGroupExternalId(UUID externalGroupId);

    boolean existsByCourseIdAndGroupId(Long courseId, Long groupId);

    @Query(value = "SELECT * FROM schedules WHERE external_id = :externalId", nativeQuery = true)
    Optional<Schedule> findAnyByExternalId(@Param("externalId")  UUID externalId);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE " +
            "s.group.id = :groupId AND " +
            "s.startTime < :newEndTime AND " +
            "s.endTime > :newStartTime")
    boolean existsByGroupIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long groupId,
            LocalDateTime newStartTime,
            LocalDateTime newEndTime
            );

    @Query(value = "SELECT * FROM schedules s " +
            "WHERE s.course_id = :courseId AND" +
            " s.group_id = :groupId",
            nativeQuery = true)
    Optional<Schedule> findAnyByCourseIdAndGroupId(
            Long courseId,
            Long groupId
    );

    @Query(value = "SELECT s FROM Schedule s " +
            "JOIN s.course c " +
            "JOIN c.teacher t " +
            "WHERE t.externalId = :teacherExternalId")
    Page<Schedule> findAllByCourseId(@Param("teacherExternalId") UUID teacherExternalId,  Pageable pageable);
}

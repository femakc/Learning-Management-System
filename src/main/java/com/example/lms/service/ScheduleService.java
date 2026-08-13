package com.example.lms.service;

import com.example.lms.dto.ScheduleRequestDto;
import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.models.Schedule;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleService {
    ScheduleResponseDto findByExternalId(UUID externalId);
    ScheduleResponseDto saveSchedule(ScheduleRequestDto scheduleRequestDto);
    ScheduleResponseDto updateSchedule(UUID scheduleId, ScheduleRequestDto scheduleRequestDto);
    void deleteSchedule(UUID scheduleId);
    ScheduleResponseDto restoreSchedule(UUID externalId);
    ScheduleResponseDto findScheduleByGroupId(UUID groupId);
    List<UUID> getPastScheduleIds(LocalDateTime pastDateTime);
    Page<ScheduleResponseDto> findByTeacherId(
            UUID teacherExternalId,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    Page<ScheduleResponseDto> findAllSchedulesByExternalId(
            int page,
            int size,
            String sortBy,
            String sortDir
    );
}

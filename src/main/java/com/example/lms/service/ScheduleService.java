package com.example.lms.service;

import com.example.lms.dto.ScheduleRequestDto;
import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.models.Schedule;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ScheduleService {
    ScheduleResponseDto findByExternalId(UUID externalId);
    ScheduleResponseDto saveSchedule(ScheduleRequestDto scheduleRequestDto);
    ScheduleResponseDto updateSchedule(UUID scheduleId, ScheduleRequestDto scheduleRequestDto);
    void deleteSchedule(UUID scheduleId);
    ScheduleResponseDto restoreSchedule(UUID externalId);

    Page<ScheduleResponseDto> findAllSchedulesByExternalId(
            int page,
            int size,
            String sortBy,
            String sortDir
    );
}

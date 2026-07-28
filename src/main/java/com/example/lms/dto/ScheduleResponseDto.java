package com.example.lms.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponseDto (
        UUID id,
//        GroupResponseDto group,
        GroupCompactDto group,
        CourseResponseDto course,
        LocalDateTime startTime,
        LocalDateTime endTime
){}

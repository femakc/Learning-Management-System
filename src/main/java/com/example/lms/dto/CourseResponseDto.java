package com.example.lms.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record CourseResponseDto (
        UUID id,
        String name,
        String description,
        TeacherResponseDto teacher,
        Set<GroupCompactDto> groups,
        LocalDateTime createdDate
){}

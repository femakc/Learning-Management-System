package com.example.lms.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record GroupResponseDto (
        UUID id,
        String name,
        Set<StudentResponseDto> students,
        LocalDateTime createdDate
){}

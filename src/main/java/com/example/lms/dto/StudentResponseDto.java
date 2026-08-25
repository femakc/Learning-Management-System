package com.example.lms.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record StudentResponseDto (
        UUID id,
        String firstName,
        String lastName,
        Set<GroupCompactDto> groups,
        LocalDateTime createdDate
){}

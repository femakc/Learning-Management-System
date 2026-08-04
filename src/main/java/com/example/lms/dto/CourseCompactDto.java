package com.example.lms.dto;

import java.util.UUID;

public record CourseCompactDto (
        UUID id,
        String name
){}

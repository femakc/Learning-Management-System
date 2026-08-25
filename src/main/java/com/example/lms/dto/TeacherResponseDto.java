package com.example.lms.dto;

import java.util.UUID;

public record TeacherResponseDto (
        UUID id,
        String firstName,
        String lastName
){}

package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record GroupRequestDto (
        @NotBlank(message = "Название группы обязательно для заполнения")
        @Size(max = 255, message = "Название группы не длиннее 255 символов")
        String name,

        Set<UUID> studentIds
){}

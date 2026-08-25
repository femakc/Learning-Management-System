package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CourseRequestDto (
        @NotBlank(message = "Название курса не должно быть пустым")
        @Size(min = 2, max = 255, message = "Название курса должно быть от 2 до 255 символов")
        String name,

        @Size(max = 1000, message = "Максимальное количество символов описания курса не более 1000 символов")
        String description,

        @NotNull(message = "У курса должен быть назначен один учитель")
        UUID teacherId,

        Set<UUID> groups
){}

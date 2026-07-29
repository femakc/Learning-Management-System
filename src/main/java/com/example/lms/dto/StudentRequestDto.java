package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record StudentRequestDto (
        @NotBlank(message = "Имя студента не может быть пустым")
        @Size(min = 2, max = 255, message = "Имя студента может быть от 2 до 255 символов")
        String firstName,

        @NotBlank(message = "Фамилия студента не может быть пустым")
        @Size(min = 2, max = 255, message = "Фамилия студента может быть от 2 до 255 символов")
        String lastName,

        @NotEmpty(message = "у студента должна быть хотя бы одна группа")
        Set<UUID> groupIds
){}

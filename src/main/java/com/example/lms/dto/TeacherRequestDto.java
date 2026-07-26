package com.example.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherRequestDto(
        @NotBlank(message = "Имя учителя не должно быть пустым")
        @Size(min = 2, max = 255, message = "Имя учителя должно содержать от 2 до 255 символов")
        String firstName,

        @NotBlank(message = "Фамилия учителя не должно быть пустым")
        @Size(min = 2, max = 255, message = "Фамилия учителя должно содержать от 2 до 255 символов")
        String lastName
) {}

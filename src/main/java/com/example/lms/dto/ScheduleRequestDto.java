package com.example.lms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleRequestDto (
        @NotNull(message = "Группа обязательна для заполнения")
        UUID groupId,

        @NotNull(message = "Курс обязателен для заполнения")
        UUID courseId,

        @NotNull(message = "Время начала курса не может быть пустым")
        @FutureOrPresent(message = "Время начала курса не может быть в прошлом")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime,

        @NotNull(message = "Время окончания курса не может быть пустым")
        @FutureOrPresent(message = "Время окончания курса не может быть в прошлом")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endTime
){}

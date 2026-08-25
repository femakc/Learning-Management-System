package com.example.lms.controller;

import com.example.lms.dto.ScheduleRequestDto;
import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.models.Schedule;
import com.example.lms.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<PagedModel<ScheduleResponseDto>> getSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "course") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<ScheduleResponseDto> schedulePage = scheduleService.findAllSchedulesByExternalId(
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<ScheduleResponseDto> response = new PagedModel<>(schedulePage);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{externalId}")
    public ResponseEntity<ScheduleResponseDto> getScheduleByExternalId (@PathVariable UUID externalId) {
        ScheduleResponseDto response = scheduleService.findByExternalId(externalId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{externalId}")
    public void deleteScheduleByExternalId (@PathVariable UUID externalId) {
        scheduleService.deleteSchedule(externalId);
    }

    @PatchMapping("/recovery-schedule/{externalId}")
    public ResponseEntity<ScheduleResponseDto> recoverySchedule (@PathVariable UUID externalId) {
        ScheduleResponseDto response = scheduleService.restoreSchedule(externalId);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ScheduleResponseDto> createSchedule (
            @Valid
            @RequestBody ScheduleRequestDto scheduleRequestDto)
    {
        ScheduleResponseDto response = scheduleService.saveSchedule(scheduleRequestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-schedule/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule (
            @Valid
            @PathVariable UUID scheduleId,
            @RequestBody ScheduleRequestDto scheduleRequestDto)
    {
        ScheduleResponseDto response = scheduleService.updateSchedule(scheduleId, scheduleRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupExternalId}")
    public ResponseEntity<ScheduleResponseDto> getScheduleByGroupId (
            @PathVariable UUID groupExternalId
    ) {
        ScheduleResponseDto response = scheduleService.findScheduleByGroupId(groupExternalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherExternalId}")
    public ResponseEntity<PagedModel<ScheduleResponseDto>> getScheduleByTeacherId (
            @PathVariable UUID teacherExternalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "course") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir

    ) {
        Page<ScheduleResponseDto> schedulePage = scheduleService.findByTeacherId(
                teacherExternalId,
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<ScheduleResponseDto> response = new PagedModel<>(schedulePage);
        return ResponseEntity.ok(response);
    }
}

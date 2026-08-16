package com.example.lms.controller;

import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.models.Teacher;
import com.example.lms.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {
    final private TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponseDto>
    createTeacher(@Valid @RequestBody TeacherRequestDto teacherRequestDto) {
        TeacherResponseDto response = teacherService.saveTeacher(teacherRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PagedModel<TeacherResponseDto>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<TeacherResponseDto> teachersPage = teacherService.findAllTeachersWithPagination(
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<TeacherResponseDto> response = new PagedModel<>(teachersPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<TeacherResponseDto>
    getTeacherByExternalId(@PathVariable UUID externalId) {
        TeacherResponseDto response = teacherService.getTeacherByExternalId(externalId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{externalId}")
    public void deleteTeacher(@PathVariable UUID externalId) {
        teacherService.deleteTeacherByExternalId(externalId);
    }

    @PatchMapping("/recovery-teacher/{externalId}")
    public ResponseEntity<TeacherResponseDto> restoreTeacher(
            @PathVariable UUID externalId
    )
    {
        TeacherResponseDto teacher = teacherService.restoreTeacherByExternalId(externalId);
        return new ResponseEntity<>(teacher, HttpStatus.OK);
    }

    @PutMapping("/update-teacher/{externalId}")
    public ResponseEntity<TeacherResponseDto> updateTeacher(
            @Valid
            @PathVariable UUID externalId,
            @RequestBody TeacherRequestDto teacherRequestDto) {
        TeacherResponseDto response = teacherService.updateTeacherByExternalId(externalId, teacherRequestDto);
        return ResponseEntity.ok(response);
    }
}

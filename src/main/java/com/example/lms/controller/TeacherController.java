package com.example.lms.controller;

import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    //TODO настроить пагинацию
    public ResponseEntity<List<TeacherResponseDto>> getAllTeachers() {
        List<TeacherResponseDto> response = teacherService.findAllTeachers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<TeacherResponseDto>
    getTeacher(@PathVariable UUID externalId) {
        TeacherResponseDto response = teacherService.getTeacherByExternalId(externalId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{externalId}")
    public void deleteTeacher(@PathVariable UUID externalId) {
        teacherService.deleteTeacherByExternalId(externalId);
    }

    @PutMapping("/update-teacher/{externalId}")
    public ResponseEntity<TeacherResponseDto> updateTeacher(
            @Valid
            @PathVariable UUID externalId,
            @RequestBody TeacherRequestDto teacherRequestDto) {
        TeacherResponseDto response = teacherService.updateTeacherByExternalId(externalId, teacherRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //TODO сделать ручку обновления преподавателя
}

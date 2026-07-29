package com.example.lms.controller;

import com.example.lms.dto.StudentRequestDto;
import com.example.lms.dto.StudentResponseDto;
import com.example.lms.models.Student;
import com.example.lms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<PagedModel<StudentResponseDto>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Page<StudentResponseDto> studentPage = studentService.findAllStudentWithPagination(
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<StudentResponseDto> response = new PagedModel<>(studentPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<StudentResponseDto> getStudentByExternalId(@PathVariable UUID externalId){
        StudentResponseDto foundStudent = studentService.findStudentByExternalId(externalId);
        return ResponseEntity.ok(foundStudent);
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto>
    createStudent(@Valid @RequestBody StudentRequestDto studentRequestDto) {
        StudentResponseDto response = studentService.saveStudent(studentRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update-student/{externalId}")
    public ResponseEntity<StudentResponseDto> updateStudent (
            @Valid
            @RequestParam UUID externalId,
            @RequestBody StudentRequestDto studentRequestDto) {
        StudentResponseDto response = studentService.updateStudentByExternalId(externalId, studentRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-student/{externalId}")
    public void deleteStudent (@PathVariable UUID externalId) {
        studentService.deleteStudentByExternalId(externalId);
    }

}

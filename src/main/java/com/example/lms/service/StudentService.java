package com.example.lms.service;

import com.example.lms.dto.StudentRequestDto;
import com.example.lms.dto.StudentResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface StudentService {
    StudentResponseDto saveStudent(StudentRequestDto studentRequestDto);
    StudentResponseDto findStudentByExternalId(UUID externalId);
    StudentResponseDto updateStudentByExternalId(UUID externalId, StudentRequestDto studentRequestDto);
    void deleteStudentByExternalId(UUID externalId);

    Page<StudentResponseDto> findAllStudentWithPagination(int page, int size, String sortBy, String sortDir);
}

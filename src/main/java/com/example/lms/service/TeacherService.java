package com.example.lms.service;

import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TeacherService {
    TeacherResponseDto saveTeacher(TeacherRequestDto teacherRequestDto);
    TeacherResponseDto getTeacherByExternalId(UUID externalId);
    TeacherResponseDto updateTeacherByExternalId(UUID externalId,  TeacherRequestDto teacherRequestDto);
    void deleteTeacherByExternalId(UUID externalId);
    TeacherResponseDto restoreTeacherByExternalId(UUID externalId);

    Page<TeacherResponseDto> findAllTeachersWithPagination(int page, int size, String sortBy, String sortDir);
}

package com.example.lms.service;

import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.models.Teacher;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    List<TeacherResponseDto> findAllTeachers();
    TeacherResponseDto saveTeacher(TeacherRequestDto teacherRequestDto);
    TeacherResponseDto getTeacherByExternalId(UUID externalId);
    TeacherResponseDto updateTeacherByExternalId(UUID externalId,  TeacherRequestDto teacherRequestDto);
    void deleteTeacherByExternalId(UUID externalId);
}

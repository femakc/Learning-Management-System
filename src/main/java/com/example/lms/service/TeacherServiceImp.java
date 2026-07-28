package com.example.lms.service;

import com.example.lms.dao.TeacherRepository;
import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.TeacherMapper;
import com.example.lms.models.Teacher;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class TeacherServiceImp implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> findAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teachers.stream()
                .map(teacherMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public TeacherResponseDto saveTeacher(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherMapper.toEntity(teacherRequestDto);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toResponseDto(savedTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherByExternalId(UUID externalId) {
        Teacher teacher = teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено!")
                );
        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional
    public TeacherResponseDto updateTeacherByExternalId(UUID externalId,  TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено!")
                );

        teacherMapper.updateEntityFromDto(teacherRequestDto, teacher);

        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacherByExternalId(UUID externalId) {
        Teacher teacher = teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено!")
                );
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }
}

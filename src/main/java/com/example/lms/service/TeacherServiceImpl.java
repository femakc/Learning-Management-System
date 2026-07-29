package com.example.lms.service;

import com.example.lms.dao.TeacherRepository;
import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.TeacherMapper;
import com.example.lms.models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

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
                        "Преподавателя с ID " + externalId + " не найдено")
                );
        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional
    public TeacherResponseDto updateTeacherByExternalId(UUID externalId,  TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено")
                );

        teacherMapper.updateEntityFromDto(teacherRequestDto, teacher);

        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacherByExternalId(UUID externalId) {
        Teacher teacher = teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Преподавателя с ID " + externalId + " не найдено")
                );
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> findAllTeachersWithPagination(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Teacher> teachersPage = teacherRepository.findAll(pageable);

        return teachersPage.map(teacherMapper::toResponseDto);
    }
}

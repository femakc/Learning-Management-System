package com.example.lms.service;

import com.example.lms.dao.CourseRepository;
import com.example.lms.dao.TeacherRepository;
import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.exceptions.ResourceAlreadyExistsException;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.CourseMapper;
import com.example.lms.models.Course;
import com.example.lms.models.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto findByExternalId(UUID externalId) {
        Course course = courseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional
    public CourseResponseDto saveCourse(CourseRequestDto courseRequestDto) {
        return courseRepository.findByNameAny(courseRequestDto.name())
                .map(existingCourse -> processExistingCourse(existingCourse,  courseRequestDto))
                .orElseGet(() -> createNewCourse(courseRequestDto));
    }

    private CourseResponseDto processExistingCourse(Course existingCourse, CourseRequestDto courseRequestDto) {
        if (!existingCourse.getDeleted())
            throw new ResourceAlreadyExistsException(
                    "Курс с именем " + existingCourse.getName() + " существует"
            );
        existingCourse.setDeleted(false);
        existingCourse.setDescription(courseRequestDto.description());
        existingCourse.setTeacher(getTeacherOrThrow(courseRequestDto.teacherId()));
        return courseMapper.toResponseDto(existingCourse);
    }

    private CourseResponseDto createNewCourse(CourseRequestDto courseRequestDto) {
        Course newCourse = courseMapper.toEntity(courseRequestDto);
        if (courseRequestDto.teacherId() != null) {
            newCourse.setTeacher(getTeacherOrThrow(courseRequestDto.teacherId()));
        }
        return courseMapper.toResponseDto(courseRepository.save(newCourse));
    }

    private Teacher getTeacherOrThrow(UUID externalId) {
        return teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Учитель c id " + externalId + "не найден"
                ));
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourse(UUID externalId, CourseRequestDto courseRequestDto) {
        Course course = courseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
        courseMapper.updateEntity(courseRequestDto, course);
        return courseMapper.toResponseDto(course);
    }


    @Override
    @Transactional
    public CourseResponseDto restoreCourse(UUID externalId) {
        Course course = courseRepository.findAnyByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
        course.setDeleted(false);
        courseRepository.save(course);
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional
    public void deleteCourse(UUID externalId) {
        Course course = courseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
        course.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> findAllCourseByExternalId(
            int page,
            int size,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(courseMapper::toResponseDto);
    }
}

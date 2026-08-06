package com.example.lms.service;

import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    CourseResponseDto findByExternalId (UUID externalId);
    CourseResponseDto saveCourse(CourseRequestDto courseDto);
    CourseResponseDto updateCourse(UUID externalId, CourseRequestDto courseRequestDto);
    CourseResponseDto restoreCourse(UUID externalId);

    void deleteCourse(UUID externalId);

    Page<CourseResponseDto> findAllCourseByExternalId(
            int page,
            int size,
            String sortBy,
            String sortDir
    );
}

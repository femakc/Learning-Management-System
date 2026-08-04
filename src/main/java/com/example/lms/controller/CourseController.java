package com.example.lms.controller;

import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.models.Course;
import com.example.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<PagedModel<CourseResponseDto>> getAllCoursesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<CourseResponseDto> coursePage = courseService.findAllCourseByExternalId(
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<CourseResponseDto> response = new PagedModel<>(coursePage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<CourseResponseDto> getCourseByExternalId (@PathVariable UUID externalId) {
        CourseResponseDto response = courseService.findByExternalId(externalId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Valid
            @RequestBody CourseRequestDto courseRequestDto) {
        CourseResponseDto savedCourse = courseService.saveCourse(courseRequestDto);
        return ResponseEntity.ok(savedCourse);
    }

    @PatchMapping("/recovery-cource/{externalId}")
    public ResponseEntity<CourseResponseDto> restoreCourse(
            @PathVariable UUID externalId
    ) {
        CourseResponseDto response = courseService.restoreCourse(externalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-course/{externalId}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @Valid
            @PathVariable UUID externalId,
            @RequestBody CourseRequestDto courseRequestDto
    ) {
        CourseResponseDto response = courseService.updateCourse(externalId, courseRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-course/{externalId}")
    void deleteCourse(@PathVariable UUID externalId) {
        courseService.deleteCourse(externalId);
    }
}

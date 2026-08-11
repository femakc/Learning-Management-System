package com.example.lms.service;

import com.example.lms.dao.CourseRepository;
import com.example.lms.dao.GroupRepository;
import com.example.lms.dao.TeacherRepository;
import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.exceptions.ResourceAlreadyExistsException;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.CourseMapper;
import com.example.lms.models.Course;
import com.example.lms.models.Group;
import com.example.lms.models.Student;
import com.example.lms.models.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto findByExternalId(UUID externalId) {
        return courseMapper.toResponseDto(getCourseOrThrow(externalId));
    }

    @Override
    @Transactional
    public CourseResponseDto saveCourse(CourseRequestDto courseRequestDto) {
        return courseRepository.findByNameAny(courseRequestDto.name())
                .map(existingCourse -> processExistingCourse(existingCourse,  courseRequestDto))
                .orElseGet(() -> createNewCourse(courseRequestDto));
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourse(UUID externalId, CourseRequestDto courseRequestDto) {
        Course course = getCourseOrThrow(externalId);
        boolean courseNameAlreadyExist = courseRepository.existsByNameAny(courseRequestDto.name(),  externalId);
        if (courseNameAlreadyExist) {
            throw new ResourceAlreadyExistsException(
                    "Невозможно обновить курс. Имя "
                            + courseRequestDto.name() +
                            " уже занято (активным или архивным) курсом!"
            );
        }
        courseMapper.updateEntity(courseRequestDto, course);
        if (courseRequestDto.teacherId() != null) {
            course.setTeacher(getTeacherOrThrow(courseRequestDto.teacherId()));
        };

        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional
    public CourseResponseDto restoreCourse(UUID externalId) {
        Course course = getDeletedCourseOrThrow(externalId);
        course.setDeleted(false);
        courseRepository.save(course);
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional
    public void deleteCourse(UUID externalId) {
        Course course = getCourseOrThrow(externalId);
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

    @Override
    @Transactional
    public CourseResponseDto addGroupsToCourse(UUID courseId, CourseRequestDto courseRequestDto){
        Course course = getCourseOrThrow(courseId);
        addGroupsToCourse(course, courseRequestDto.groups());
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional
    public CourseResponseDto removeGroupsFromCourse(UUID courseId, CourseRequestDto courseRequestDto){
        Course course = getCourseOrThrow(courseId);
        removeGroupsFromCourse(course, courseRequestDto.groups());
        return courseMapper.toResponseDto(course);
    }

    private void removeGroupsFromCourse(Course course, Set<UUID> groupIds){
        if (groupIds != null && !groupIds.isEmpty()) {
            Set<Group> existGroups = groupRepository.findAllByExternalIdIn(groupIds);

            if (groupIds.size() != existGroups.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных групп не найдены!"
                );
            } //TODO определить нужна-ли эта проверка !

            existGroups.forEach(course::removeGroups);
        }
    }

    private void addGroupsToCourse(Course course, Set<UUID> groupIds){
        if (groupIds != null && !groupIds.isEmpty()) {
            Set<Group> existGroups = groupRepository.findAllByExternalIdIn(groupIds);

            if (groupIds.size() != existGroups.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных групп не найдены!"
                );
            }

            existGroups.forEach(course::addGroups);
        }
    }

    private Course getDeletedCourseOrThrow(UUID externalId) {
        return courseRepository.findAnyByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
    }

    private Group getGroupOrThrow(UUID externalId) {
        return groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + externalId + "не найдено"
                ));
    }

    private Course getCourseOrThrow(UUID externalId) {
        return courseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + "не найдено"
                ));
    }

    private Teacher getTeacherOrThrow(UUID externalId) {
        return teacherRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Учитель c id " + externalId + "не найден"
                ));
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
}

package com.example.lms.service;

import com.example.lms.dao.CourseRepository;
import com.example.lms.dao.GroupRepository;
import com.example.lms.dao.ScheduleRepository;
import com.example.lms.dao.StudentRepository;
import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import com.example.lms.exceptions.ResourceAlreadyExistsException;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.GroupMapper;
import com.example.lms.models.Course;
import com.example.lms.models.Group;
import com.example.lms.models.Schedule;
import com.example.lms.models.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GroupMapper groupMapper;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public GroupResponseDto saveGroup(GroupRequestDto groupRequestDto) {
        return groupRepository.findByNameAny(groupRequestDto.name())
                .map(existingGroup -> processExistingGroup(existingGroup, groupRequestDto))
                .orElseGet(() -> createNewGroup(groupRequestDto));
    }

    @Override
    @Transactional
    public GroupResponseDto updateGroupByExternalId(UUID externalId, GroupRequestDto groupRequestDto) {
        if (groupRepository.existsByNameAny(groupRequestDto.name(), externalId)) {
            throw new ResourceAlreadyExistsException(
                    "Группа с именем " + groupRequestDto.name() + " существует!"
            );
        }

        Group group = getGroupByExternalId(externalId);

        if (groupRequestDto.courseIds() != null){
            group.getCourses().clear();

            bindCoursesIfPresent(group, groupRequestDto.courseIds());
        }

        if (groupRequestDto.studentIds() != null){
            group.getStudents().forEach(student -> student.getGroups().remove(group));
            group.getStudents().clear();

            bindStudentsIfPresent(group, groupRequestDto.studentIds());
        }

        groupMapper.updateEntityFromDto(groupRequestDto, group);

        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponseDto findGroupByExternalId(UUID externalId) {
        Group group = getGroupByExternalId(externalId);
        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional
    public void deleteGroupByExternalId(UUID externalId) {
        Group group = getGroupByExternalId(externalId);
        group.setDeleted(true);

        Optional<Schedule> schedule = scheduleRepository.findByGroupId(group.getId());
        schedule.ifPresent(value -> value.setDeleted(true));
    }

    @Override
    @Transactional
    public GroupResponseDto restoreGroupByExternalId(UUID externalId) {
        Group group = getGroupByExternalId(externalId);
        group.setDeleted(false);
        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponseDto> findAllGroupsWithPagination(
            int page,
            int size,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Group> groupPage = groupRepository.findAll(pageable);
        return groupPage.map(groupMapper::toResponseDto);
    }

    @Override
    @Transactional
    public GroupResponseDto removeCourseFromGroupByExternalId(UUID groupId, UUID courseId) {
        Group group = getGroupByExternalId(groupId);
        Course course = getCourseByExternalId(courseId);

        if (!group.getCourses().contains(course)) {
            throw new ResourceNotFoundException(
                    "Указанный курс не привязан к группе"
            );
        }

        group.getCourses().remove(course);
        course.getGroups().remove(group);

        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional
    public GroupResponseDto addStudentsToGroup(UUID externalID, GroupRequestDto groupRequestDto) {
        Group group = getGroupByExternalId(externalID);
        bindStudentsIfPresent(group, groupRequestDto.studentIds());
        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional
    public GroupResponseDto deleteStudentFromGroup(UUID externalId, GroupRequestDto groupRequestDto) {
        Group group = getGroupByExternalId(externalId);
        removeStudentsFromGroup(group, groupRequestDto.studentIds());
        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional
    public GroupResponseDto addGroupFromCourse(UUID groupId, UUID courseId) {
        Group group = getGroupByExternalId(groupId);
        Course course = getCourseByExternalId(courseId);
        group.getCourses().add(course);
        course.getGroups().add(group);
        return groupMapper.toResponseDto(group);
    }

    private void removeStudentsFromGroup(Group group, Set<UUID> studentIds) {
        if (studentIds != null && !studentIds.isEmpty()) {
            Set<Student> existingStudents = studentRepository.findByExternalIdIn(studentIds);

            if (studentIds.size() != existingStudents.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных студентов не найдены!"
                );
            }
            existingStudents.forEach(group::removeStudents);
        }
    }

    private GroupResponseDto processExistingGroup(Group existingGroup, GroupRequestDto groupRequestDto) {
        if (!existingGroup.getDeleted())
            throw new ResourceAlreadyExistsException(
                    "Группа с именем " + existingGroup.getName() + " существует"
            );
        existingGroup.setDeleted(false);

        bindCoursesIfPresent(existingGroup, groupRequestDto.courseIds());

        return groupMapper.toResponseDto(existingGroup);
    }

    private GroupResponseDto createNewGroup(GroupRequestDto groupRequestDto) {
        Group savedGroup = groupRepository.save(groupMapper.toEntity(groupRequestDto));
        return groupMapper.toResponseDto(savedGroup);
    }

    private void bindStudentsIfPresent(Group group, Set<UUID> studentIds) {
        if (studentIds != null && !studentIds.isEmpty()) {
            Set<Student> existingStudents = studentRepository.findByExternalIdIn(studentIds);

            if (studentIds.size() != existingStudents.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных студентов не найдены!"
                );
            }

            existingStudents.forEach(group::addStudents);
        }
    }

    private void bindCoursesIfPresent(Group group, Set<UUID> courseIds) {

        if (courseIds != null && !courseIds.isEmpty()) {
            Set<Course> foundCourses = courseRepository.findByExternalIdIn(courseIds);

            if (courseIds.size() != foundCourses.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных курсов не найдены!"
                );
            }
            group.setCourses(foundCourses);
        }
    }

    private Course getCourseByExternalId(UUID externalId) {
        return courseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + externalId + " не найдено"
                ));
    }

    private Group getGroupByExternalId(UUID externalId) {
        return groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + externalId + " не найдено!"
                ));
    }
}

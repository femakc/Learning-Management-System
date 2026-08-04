package com.example.lms.service;

import com.example.lms.dao.CourseRepository;
import com.example.lms.dao.GroupRepository;
import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import com.example.lms.exceptions.ResourceAlreadyExistsException;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.GroupMapper;
import com.example.lms.models.Course;
import com.example.lms.models.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final GroupMapper groupMapper;

    @Override
    @Transactional
    public GroupResponseDto saveGroup(GroupRequestDto groupRequestDto) {
        return groupRepository.findByNameAny(groupRequestDto.name())
                .map(existingGroup -> processExistingGroup(existingGroup, groupRequestDto))
                .orElseGet(() -> createNewGroup(groupRequestDto));
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

    private void bindCoursesIfPresent(Group group, Set<UUID> courseIds) {

        if (courseIds != null && !courseIds.isEmpty()) {
            Set<Course> foundCourses = courseRepository.findByExternalIdIn(courseIds);

            if (courseIds.size() != foundCourses.size()) {
                throw new ResourceNotFoundException(
                        "Один или несколько переданных курсов не найдены"
                );
            }
            group.setCourses(foundCourses);
        }
    }

    @Override
    @Transactional
    public GroupResponseDto updateGroupByExternalId(UUID externalId, GroupRequestDto groupRequestDto) {
        Group group = groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с ID " + externalId + " не найдена"
                ));
        groupMapper.updateEntityFromDto(groupRequestDto, group);

        if (groupRequestDto.courseIds() != null){
            group.getCourses().clear();

            bindCoursesIfPresent(group, groupRequestDto.courseIds());
        }

        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponseDto findGroupByExternalId(UUID externalId) {
        Group group = groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с ID " + externalId + " не найдена"
                ));
        return groupMapper.toResponseDto(group);
    }

    @Override
    @Transactional
    public void deleteGroupByExternalId(UUID externalId) {
        Group group = groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с ID " + externalId + " не найдена"
                ));
        group.setDeleted(true);
    }

    @Override
    public GroupResponseDto restoreGroupByExternalId(UUID externalId) {
        Group group = groupRepository.findAnyByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + externalId + "не найдено"
                ));
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
        Group group = groupRepository.findByExternalId(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + groupId + " не найдено"
                ));
        Course course = courseRepository.findByExternalId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + courseId + " не найдено"
                ));
        if (!group.getCourses().contains(course)) {
            throw new ResourceNotFoundException(
                    "Указанный курс не привязан к группе"
            );
        }

        group.getCourses().remove(course);
        course.getGroups().remove(group);

        return groupMapper.toResponseDto(group);
    }
}

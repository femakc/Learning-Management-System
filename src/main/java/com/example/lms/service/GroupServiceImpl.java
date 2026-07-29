package com.example.lms.service;

import com.example.lms.dao.GroupRepository;
import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.GroupMapper;
import com.example.lms.models.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @Override
    @Transactional
    public GroupResponseDto saveGroup(GroupRequestDto groupRequestDto) {
        Group group = groupMapper.toEntity(groupRequestDto);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toResponseDto(savedGroup);
    }

    @Override
    @Transactional
    public GroupResponseDto updateGroupByExternalId(UUID externalId, GroupRequestDto groupRequestDto) {
        Group group = groupRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с ID " + externalId + " не найдена"
                ));
        groupMapper.updateEntityFromDto(groupRequestDto, group);
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
}

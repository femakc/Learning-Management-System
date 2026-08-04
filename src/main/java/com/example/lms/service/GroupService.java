package com.example.lms.service;

import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GroupService {
    GroupResponseDto saveGroup(GroupRequestDto groupRequestDto);
    GroupResponseDto updateGroupByExternalId(UUID externalId, GroupRequestDto groupRequestDto);
    GroupResponseDto findGroupByExternalId(UUID externalId);
    void deleteGroupByExternalId(UUID externalId);
    GroupResponseDto restoreGroupByExternalId(UUID externalId);
    GroupResponseDto removeCourseFromGroupByExternalId(UUID groupId, UUID courseId);

    Page<GroupResponseDto> findAllGroupsWithPagination(int page, int size, String sortBy, String sortDir);
}

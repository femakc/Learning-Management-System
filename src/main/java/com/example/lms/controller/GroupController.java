package com.example.lms.controller;

import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import com.example.lms.models.Group;
import com.example.lms.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<PagedModel<GroupResponseDto>> getAllGroupsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<GroupResponseDto> groupsPage = groupService.findAllGroupsWithPagination(
                page,
                size,
                sortBy,
                sortDir
        );
        PagedModel<GroupResponseDto> response = new PagedModel<>(groupsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<GroupResponseDto> getGroupByExternalId (@PathVariable UUID externalId) {
        GroupResponseDto response = groupService.findGroupByExternalId(externalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-group/{externalId}")
    public ResponseEntity<GroupResponseDto> updateGroupByExternalId (
            @Valid
            @PathVariable UUID externalId,
            @RequestBody GroupRequestDto groupRequestDto
    ){
        GroupResponseDto group = groupService.updateGroupByExternalId(externalId, groupRequestDto);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/delete-group/{externalId}")
    public void deleteGroup(@PathVariable UUID externalId){
        groupService.deleteGroupByExternalId(externalId);
    }

    @DeleteMapping("/{groupId}/course/{courseId}")
    public void deleteGroupCourse(@PathVariable UUID groupId, @PathVariable UUID courseId){
        groupService.removeCourseFromGroupByExternalId(groupId, courseId);
    }

    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup (
            @Valid
            @RequestBody GroupRequestDto groupRequestDto
    ){
        GroupResponseDto savedGroup = groupService.saveGroup(groupRequestDto);
        return ResponseEntity.ok(savedGroup);
    }

    @PatchMapping("/recovery-group/{externalId}")
    public ResponseEntity<GroupResponseDto> recoveryGroup (
            @PathVariable UUID externalId
    ) {
        GroupResponseDto response = groupService.restoreGroupByExternalId(externalId);
        return ResponseEntity.ok(response);
    }
}

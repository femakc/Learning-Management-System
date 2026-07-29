package com.example.lms.controller;

import com.example.lms.dto.GroupResponseDto;
import com.example.lms.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<PagedModel<GroupResponseDto>> findAllGroupsWithPagination(
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


}

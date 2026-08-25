package com.example.lms.mappers;

import com.example.lms.dto.CourseCompactDto;
import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.dto.GroupCompactDto;
import com.example.lms.models.Course;
import com.example.lms.models.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TeacherMapper.class})
public interface CourseMapper {

    @Mapping(source = "externalId", target = "id")
    CourseResponseDto toResponseDto(Course course);

    default Set<GroupCompactDto> mapGroupsToCompactDto(Set<Group> groups) {
        if (groups == null) {
            return Collections.emptySet();
        }
        return groups.stream()
                .map(group -> new GroupCompactDto(group.getExternalId(), group.getName()))
                .collect(Collectors.toSet());
    }

    @Mapping(source = "externalId", target = "id")
    GroupCompactDto toCompactDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Course toEntity(CourseRequestDto courseDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groups", ignore = true)
    void updateEntity(CourseRequestDto courseRequestDto, @MappingTarget Course courseDto);
}

package com.example.lms.mappers;

import com.example.lms.dto.*;
import com.example.lms.models.Course;
import com.example.lms.models.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CourseMapper.class, StudentMapper.class})
public interface GroupMapper {

    @Mapping(source = "externalId", target = "id")
    @Mapping(target = "courses", expression = "java(mapCourseToCompactDto(group.getCourses()))")
    GroupResponseDto toResponseDto(Group group);

    default Set<CourseCompactDto> mapCourseToCompactDto(Set<Course> courses) {
        if (courses == null) {
            return Collections.emptySet();
        }
        return courses.stream()
                .map(course -> new CourseCompactDto(course.getExternalId(), course.getName()))
                .collect(Collectors.toSet());
    }

    @Mapping(source = "externalId", target = "id")
    GroupCompactDto toCompactDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Group toEntity(GroupRequestDto groupRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromDto(GroupRequestDto groupRequestDto, @MappingTarget Group group);
}

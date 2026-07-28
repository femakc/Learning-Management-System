package com.example.lms.mappers;

import com.example.lms.dto.CourseRequestDto;
import com.example.lms.dto.CourseResponseDto;
import com.example.lms.models.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TeacherMapper.class})
public interface CourseMapper {

    @Mapping(source = "externalId", target = "id")
    CourseResponseDto toResponseDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseRequestDto courseDto);
}

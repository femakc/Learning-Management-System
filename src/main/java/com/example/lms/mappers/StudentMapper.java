package com.example.lms.mappers;

import com.example.lms.dto.GroupCompactDto;
import com.example.lms.dto.StudentRequestDto;
import com.example.lms.dto.StudentResponseDto;
import com.example.lms.models.Group;
import com.example.lms.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(source = "externalId", target = "id")
    StudentResponseDto toResponseDto(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Student toEntity(StudentRequestDto studentRequestDto);

    default Set<GroupCompactDto> mapGroups(Set<Group> groups) {
        if (groups == null) {
            return Collections.emptySet();
        }
        return groups.stream()
                .map(group -> new GroupCompactDto(group.getExternalId(), group.getName()))
                .collect(Collectors.toSet());}

}

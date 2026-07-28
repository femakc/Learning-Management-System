package com.example.lms.mappers;

import com.example.lms.dto.TeacherRequestDto;
import com.example.lms.dto.TeacherResponseDto;
import com.example.lms.models.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(source = "externalId", target = "id")
    TeacherResponseDto toResponseDto(Teacher teacher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Teacher toEntity(TeacherRequestDto teacherRequestDto);

    //TODO Обновление существующей сущности из DTO (Для PUT/PATCH запросов) возможно не понадобиться
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(TeacherRequestDto requestDto, @MappingTarget Teacher teacher);
}

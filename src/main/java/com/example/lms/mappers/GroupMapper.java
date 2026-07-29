package com.example.lms.mappers;

import com.example.lms.dto.GroupCompactDto;
import com.example.lms.dto.GroupRequestDto;
import com.example.lms.dto.GroupResponseDto;
import com.example.lms.models.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface GroupMapper {

    @Mapping(source = "externalId", target = "id")
    GroupResponseDto toResponseDto(Group group);

    @Mapping(source = "externalId", target = "id")
    GroupCompactDto toCompactDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "students", ignore = true)
//    @Mapping(target = "groups", ignore = true)
    Group toEntity(GroupRequestDto groupRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
//    @Mapping(target = "students", ignore = true)
    void updateEntityFromDto(GroupRequestDto groupRequestDto, @MappingTarget Group group);
}

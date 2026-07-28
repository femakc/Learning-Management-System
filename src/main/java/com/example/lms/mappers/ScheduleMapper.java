package com.example.lms.mappers;

import com.example.lms.dto.ScheduleRequestDto;
import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.models.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GroupMapper.class, CourseMapper.class})
public interface ScheduleMapper {

    @Mapping(source = "externalId", target = "id")
    ScheduleResponseDto toResponseDto(Schedule schedule);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "group", ignore = true)  // Игнорируем, свяжем по UUID вручную в Service
    @Mapping(target = "course", ignore = true)
    Schedule toEntity(ScheduleRequestDto scheduleRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "group", ignore = true)  // Игнорируем, связи обновляются в сервисе вручную
    @Mapping(target = "course", ignore = true) // Игнорируем, связи обновляются в сервисе вручную
    void updateEntityFromDto(ScheduleRequestDto requestDto, @MappingTarget Schedule schedule);
}

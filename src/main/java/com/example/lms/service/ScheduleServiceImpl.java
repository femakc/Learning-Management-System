package com.example.lms.service;

import com.example.lms.dao.CourseRepository;
import com.example.lms.dao.GroupRepository;
import com.example.lms.dao.ScheduleRepository;
import com.example.lms.dto.ScheduleRequestDto;
import com.example.lms.dto.ScheduleResponseDto;
import com.example.lms.exceptions.ResourceAlreadyExistsException;
import com.example.lms.exceptions.ResourceIllegalArgumentException;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.ScheduleMapper;
import com.example.lms.models.BaseEntity;
import com.example.lms.models.Group;
import com.example.lms.models.Course;
import com.example.lms.models.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponseDto findByExternalId(UUID externalId) {
        return scheduleMapper.toResponseDto(getScheduleOrThrow(externalId));
    }

    @Override
    @Transactional
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto scheduleRequestDto) {

        //Проверка времени начало не позже окончания и наоборот
        validateTimeInterval(scheduleRequestDto.startTime(), scheduleRequestDto.endTime());

        Course course = getCourseOrThrow(scheduleRequestDto);
        Group group = getGroupOrThrow(scheduleRequestDto);

        //Проверка запланированного расписания. Требуется только когда создаем расписание!
        validateTimeOverLapping(
                group,
                scheduleRequestDto.startTime(),
                scheduleRequestDto.endTime()
        );

        //Если группа или курс находятся в удаленных, не создаем новые, а воскрешаем их
        return scheduleRepository.findAnyByCourseIdAndGroupId(course.getId(), group.getId())
                .map(existingSchedule -> processExistingSchedule(
                        group,
                        course,
                        existingSchedule,
                        scheduleRequestDto))
                .orElseGet(() -> createNewSchedule(group, course, scheduleRequestDto));
    }

    @Override
    @Transactional
    public ScheduleResponseDto updateSchedule(
            UUID scheduleId,
            ScheduleRequestDto scheduleRequestDto) {

        Schedule schedule = getScheduleOrThrow(scheduleId);

        validateTimeInterval(scheduleRequestDto.startTime(), scheduleRequestDto.endTime());
        validateTimeOverLapping(
                schedule.getGroup(),
                scheduleRequestDto.startTime(),
                scheduleRequestDto.endTime()
        );
        schedule.setStartTime(scheduleRequestDto.startTime());
        schedule.setEndTime(scheduleRequestDto.endTime());
        return scheduleMapper.toResponseDto(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID scheduleId) {
        Schedule schedule = getScheduleOrThrow(scheduleId);
        schedule.setDeleted(true);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponseDto restoreSchedule(UUID externalId) {
        Schedule schedule = scheduleRepository.findAnyByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Расписания с ID " + externalId + " не найдено!"
                ));
        schedule.setDeleted(false);
        scheduleRepository.save(schedule);
        return scheduleMapper.toResponseDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> findAllSchedulesByExternalId(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Schedule> coursePage = scheduleRepository.findAll(pageable);
        return coursePage.map(scheduleMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponseDto findScheduleByGroupId(UUID groupExternalId) {
        Schedule schedule = scheduleRepository.findByGroupExternalId(groupExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "У группы с ID" + groupExternalId + " нет расписаний!"
                ));
        return scheduleMapper.toResponseDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> findByTeacherId(
            UUID teacherExternalId,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Schedule> coursePage = scheduleRepository.findAllByCourseId(
                teacherExternalId,
                pageable
        );
        return coursePage.map(scheduleMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getPastScheduleIds(LocalDateTime pastDateTime) {
        List<Schedule> schedules = scheduleRepository.findByStartTimeBefore(pastDateTime);
        return schedules.stream().map(Schedule::getExternalId).collect(Collectors.toList());
    }

    private Schedule getScheduleOrThrow(UUID externalId) {
        return scheduleRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Расписание с ID " + externalId + " не найдено!"
                ));
    }

    private void validateTimeInterval(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new ResourceIllegalArgumentException(
                    "Время начала занятия должно быть строго раньше времени окончания!"
            );
        }
    }

    private Group getGroupOrThrow(ScheduleRequestDto scheduleRequestDto) {
        return groupRepository.findByExternalId(scheduleRequestDto.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группа с ID " + scheduleRequestDto.groupId() + " не найдена!"
                ));
    }

    private Course getCourseOrThrow(ScheduleRequestDto scheduleRequestDto) {
        return courseRepository.findByExternalId(scheduleRequestDto.courseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курса с ID " + scheduleRequestDto.courseId() + " не найдено!"
                ));
    }

    private void validateExistByCourseIdAndGroupId(Course course, Group group) {
        if (scheduleRepository.existsByCourseIdAndGroupId(
                course.getId(), group.getId()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Расписание с названием курса " + course.getName() +
                            " и группой " + group.getName() + " существует!"
            );
        }
    }

    private void validateTimeOverLapping(Group group, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        boolean timeIsNotValidate = scheduleRepository.existsByGroupIdAndStartTimeLessThanAndEndTimeGreaterThan(
                group.getId(),
                newStartTime,
                newEndTime
        );
        if (timeIsNotValidate) {
            throw new ResourceNotFoundException(
                    "У группы " + group.getName() + "в этот временной интервал уже запланировано занятие!"
            );
        }
    }

    private ScheduleResponseDto processExistingSchedule(
            Group group,
            Course course,
            Schedule existSchedule,
            ScheduleRequestDto scheduleRequestDto) {

        //Проверка наличия расписания для курса и группы при создании расписания
        validateExistByCourseIdAndGroupId(course, group);

        existSchedule.setDeleted(false);
        existSchedule.setCourse(course);
        existSchedule.setGroup(group);
        existSchedule.setStartTime(scheduleRequestDto.startTime());
        existSchedule.setEndTime(scheduleRequestDto.endTime());
        return scheduleMapper.toResponseDto(existSchedule);
    }

    private ScheduleResponseDto createNewSchedule(Group group, Course course, ScheduleRequestDto scheduleRequestDto) {
        Schedule newSchedule = scheduleMapper.toEntity(scheduleRequestDto);
        newSchedule.setCourse(course);
        newSchedule.setGroup(group);
        newSchedule.setStartTime(scheduleRequestDto.startTime());
        newSchedule.setEndTime(scheduleRequestDto.endTime());
        return scheduleMapper.toResponseDto(newSchedule);
    }
}

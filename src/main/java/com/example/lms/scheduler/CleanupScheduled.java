package com.example.lms.scheduler;

import com.example.lms.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupScheduled {

    private final ScheduleService scheduleService;
    private final SchedulerProperties schedulerProperties;

    @Scheduled(cron = "#{@schedulerProperties.cleanupSchedulePastTowYear}")
    public void deleteOldSchedules() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoYearsAgo = now.minusYears(2);

        List<UUID> scheduleIds = scheduleService.getPastScheduleIds(twoYearsAgo);
        scheduleIds.forEach(scheduleService::deleteSchedule);
    }
}

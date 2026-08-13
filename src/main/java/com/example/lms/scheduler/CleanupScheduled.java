package com.example.lms.scheduler;

import com.example.lms.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CleanupScheduled {

    private final ScheduleService scheduleService;

    @Scheduled(cron = "* * 0 * * ?")
    public void deleteOldSchedules() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoYearsAgo = now.minusYears(2);

        List<UUID> scheduleIds = scheduleService.getPastScheduleIds(twoYearsAgo);
        scheduleIds.forEach(scheduleService::deleteSchedule);
    }
}

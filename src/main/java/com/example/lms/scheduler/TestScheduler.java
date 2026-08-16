package com.example.lms.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("dev")
public class TestScheduler {
    private final SchedulerProperties schedulerProperties;

    @Scheduled(cron = "#{@schedulerProperties.testScheduler}")
    public void testScheduler() {
        log.info("Test testScheduler started");
        log.info("Current cron getTestScheduler: {}", schedulerProperties.getTestScheduler());
        log.info("Current cron getCleanupSchedulePastTowYear: {}", schedulerProperties.getCleanupSchedulePastTowYear());
    }
}

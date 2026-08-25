package com.example.lms.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "scheduler.tasks")
public class SchedulerProperties {
    private String cleanupSchedulePastTowYear = "0 0 0 * * ?";
    private String testScheduler ="*/10 * * * * ?";

    @PostConstruct
    public void init() {
        log.info("=== SCHEDULER PROPERTIES ===");
        log.info("cleanupSchedulesPastTwoYear: " + cleanupSchedulePastTowYear);
        log.info("testScheduler: " + testScheduler);
        log.info("==============================");
    }
}

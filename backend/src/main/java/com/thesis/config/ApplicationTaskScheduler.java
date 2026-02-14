package com.thesis.config;

import com.thesis.service.TeacherStudentSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ApplicationTaskScheduler {

    @Autowired
    private TeacherStudentSelectionService selectionService;

    // 每小时检查一次超时的申请
    @Scheduled(cron = "0 0 * * * *")
    public void processTimeoutApplications() {
        selectionService.checkAndProcessTimeoutApplications();
    }
}

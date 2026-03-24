package com.scheduling.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

@Slf4j
public class DummyEmailTask extends SimpleBaseJob {

    @Override
    protected void executeJob(JobExecutionContext context) throws Exception {
        var dataMap = context.getJobDetail().getJobDataMap();
        String recipientEmail = dataMap.containsKey("param_recipientEmail")
            ? dataMap.getString("param_recipientEmail")
            : "unknown@example.com";
        String emailSubject = dataMap.containsKey("param_subject")
            ? dataMap.getString("param_subject")
            : "Scheduled Task Email";
        String emailBody = dataMap.containsKey("param_body")
            ? dataMap.getString("param_body")
            : "This is a dummy email from the scheduling system.";

        log.info("DUMMY EMAIL TASK");
        log.info("To: {}", recipientEmail);
        log.info("Subject: {}", emailSubject);
        log.info("Body: {}", emailBody);
        log.info("(This is a dummy task - no actual email is sent)");
    }
}

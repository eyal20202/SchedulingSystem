package com.scheduling.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

@Slf4j
public class LogTask extends SimpleBaseJob {

    @Override
    protected void executeJob(JobExecutionContext context) throws Exception {
        var dataMap = context.getJobDetail().getJobDataMap();
        String message = dataMap.containsKey("param_message")
            ? dataMap.getString("param_message")
            : "Log Task executed";

        log.info("LOG MESSAGE: {}", message);
    }
}

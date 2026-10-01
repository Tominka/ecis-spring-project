package cz.ecis.config.scheduler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cz.ecis.core.scheduler.ISchedulableJob;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SchedulerConfig {

    private static final Logger LOGGER = LogManager.getLogger(SchedulerConfig.class);

    private final Scheduler scheduler;

    @Bean
    Scheduler scheduler(@Autowired List<ISchedulableJob> jobs) throws SchedulerException {

        for (final ISchedulableJob job : jobs) {
            JobKey key = job.getJobDetail().getKey();
            if (scheduler.checkExists(key)) {
                SchedulerConfig.LOGGER.warn("Job {} is already registered. Recreating...", key);
                scheduler.deleteJob(key);
            }
            scheduler.scheduleJob(job.getJobDetail(), job.getTrigger());
        }

        return scheduler;
        
    }
    
}

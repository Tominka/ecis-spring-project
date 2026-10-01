package cz.ecis.core.scheduler;

import java.util.Optional;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

public interface ISchedulableJob extends Job {

    @Override
    default void execute(JobExecutionContext context) throws JobExecutionException {
        int attempt = 0;
        int maxAttempts = this.getMaxRetries().orElse(1);
        while (attempt < maxAttempts) {
            try {
                this.onTrigger(context);
                return;
            } catch (Exception e) {
                attempt++;
                
                if (attempt >= maxAttempts) {
                    if (this.onException(e)) {
                        throw new JobExecutionException(e, false);
                    } else {
                        return;
                    }
                }

                try {
                    Thread.sleep(this.getRetryDelay().orElse(1000));
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    void onTrigger(JobExecutionContext context);

    boolean onException(Throwable e);
    
    JobDetail getJobDetail();

    Trigger getTrigger();

    Optional<Integer> getMaxRetries();

    Optional<Integer> getRetryDelay();
}

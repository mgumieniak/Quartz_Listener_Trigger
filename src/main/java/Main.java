import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        String key = "key";
        Boolean value = false;
        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        JobDetail job = newJob(HelloJob.class)
                .ofType(HelloJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData(key,value)
                .build();

        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();

        sched.scheduleJob(job, trigger);

        sched.getListenerManager().addJobListener(new MyJobListener(), KeyMatcher.keyEquals(job.getKey()));
        sched.getListenerManager().addTriggerListener(new MyTriggerListener(),KeyMatcher.keyEquals(trigger.getKey()));

        sched.start();

        Thread.sleep(5000L);
        value = true;

//        sched.deleteJob(jobKey);

        sched.shutdown(true);
    }


}

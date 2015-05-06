package edu.yale.library.ladybird.kernel.cron;

import org.quartz.JobDetail;

import java.util.List;

/**
 * Keeps track of which ron jobs are scheduled.
 * Used for un- and re-scheduling
 */
public interface ScheduledJobs {

    List<JobDetail> getJobs();

    void setJobs(List<JobDetail> jobs);
}

package edu.yale.library.engine.cron;

import org.quartz.JobDetail;

import java.util.List;


/**
 * For now data structure for all jobs
 */
public interface JobsManager {

    public List<JobDetail> getJobs();

    public void setJobs(List<JobDetail> jobs) ;
}

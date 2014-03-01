package edu.yale.library.cron;

import org.quartz.JobDetail;

import java.util.List;


public interface JobsManager {

    List<JobDetail> getJobs();

    void setJobs(List<JobDetail> jobs);
}

package edu.yale.library.cron;

import org.quartz.JobDetail;

import java.util.List;


public interface JobsManager
{

    public List<JobDetail> getJobs();

    public void setJobs(List<JobDetail> jobs) ;
}

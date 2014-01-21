package edu.yale.library.engine;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;

import javax.inject.Named;


/**
 * For now data structure for all jobs
 */
public interface JobsManager {

    public List<JobDetail> getJobs();

    public void setJobs(List<JobDetail> jobs) ;
}

package edu.yale.library.engine;

import org.quartz.*;
import org.slf4j.Logger;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * For now data structure for all jobs
 */
@Named
public class DefaultJobsManager implements JobsManager
{
    private final Logger logger = getLogger(this.getClass());

    //TODO
    List<JobDetail> jobs = new ArrayList<JobDetail>();

    {
        JobDetail job = JobBuilder.newJob(new Job() {
            @Override
            public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                //noop
            }
        }.getClass()).withIdentity("cleanup").build();
        jobs.add(job);
    }

    public List<JobDetail> getJobs()
    {
        return jobs;
    }

    @Override
    public String toString() {
        return "DefaultJobsManager{" +
                "jobs=" + jobs +
                '}';
    }

    public void setJobs(List<JobDetail> jobs)
    {
        this.jobs = jobs;
    }
}

package edu.yale.library.cron;

import org.quartz.JobDetail;
import org.slf4j.Logger;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;


@Named
public class DefaultJobsManager implements JobsManager {
    private final Logger logger = getLogger(this.getClass());

    static List<JobDetail> jobs = new ArrayList<>(); //TODO

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void addJob(JobDetail job) {
        jobs.add(job);
    }

    @Override
    public String toString() {
        return "DefaultJobsManager{"
                + "jobs=" + jobs
                + '}';
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }
}

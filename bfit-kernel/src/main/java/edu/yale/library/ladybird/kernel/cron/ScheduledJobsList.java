package edu.yale.library.ladybird.kernel.cron;

import org.quartz.JobDetail;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;


@Named
public class ScheduledJobsList implements ScheduledJobs {

    private static List<JobDetail> jobs = new ArrayList<>(); //FIXME

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void addJob(JobDetail job) {
        jobs.add(job);
    }

    @Override
    public String toString() {
        return " ScheduledJobsList{"
                + "jobs=" + jobs
                + '}';
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }
}

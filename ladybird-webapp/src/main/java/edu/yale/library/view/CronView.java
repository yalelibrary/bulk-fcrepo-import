package edu.yale.library.view;


import edu.yale.library.engine.cron.DefaultJobsManager;
import edu.yale.library.engine.cron.JobsManager;
import org.quartz.JobDetail;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class CronView {

    private final Logger logger = getLogger(this.getClass());

    List<JobDetail> jobs = new ArrayList();

    @PostConstruct
    public void init()
    {
        //logger.debug("Inst jobs");
        JobsManager jobsManager = new DefaultJobsManager();
        jobs = jobsManager.getJobs();
        //logger.debug("Jobs size=" + jobs.size());

        //logger.debug("val=" + jobs.get(0).getKey().getName());
        //logger.debug("done inst jobs");
    }

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }
}

package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.kernel.cron.JobsManager;
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
@SuppressWarnings("unchecked")
public class CronView {

    private final Logger logger = getLogger(this.getClass());

    private List<JobDetail> jobs = new ArrayList();

    @PostConstruct
    public void init() {
        JobsManager jobsManager = new DefaultJobsManager();
        jobs = jobsManager.getJobs();
    }

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }
}

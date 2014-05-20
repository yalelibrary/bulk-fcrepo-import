package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.kernel.cron.JobsManager;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class CronView extends AbstractView {

    private final Logger logger = getLogger(this.getClass());

    /** list of Quartz jobs */
    private List<JobDetail> jobs = new ArrayList();

    private JobDetail selectedItem = new JobDetailImpl();

    @Inject
    private ImportScheduler importJobScheduler;

    @Inject
    private ExportScheduler exportScheduler;

    @PostConstruct
    public void init() {
        initFields();
        final JobsManager jobsManager = new DefaultJobsManager();
        jobs = jobsManager.getJobs();
    }

    public List<JobDetail> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDetail> jobs) {
        this.jobs = jobs;
    }

    public JobDetail getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(JobDetail selectedItem) {
        this.selectedItem = selectedItem;
    }

    //TODO gets job name but cancels whole group. This is ok for now since there's only one import or export cron.
    //TODO check/report exception
    public String unscheduleJob() {
        final String jobIdentifier = selectedItem.getKey().toString();
        boolean jobRemoved = false;
        logger.debug("Unscheduling job={}", jobIdentifier);
        if (jobIdentifier.equals(getImportJobIdentifier())) {
            importJobScheduler.cancel();
            jobRemoved = removeJob(jobIdentifier);
        } else if (jobIdentifier.equals(getExportJobIdentifier())) {
            exportScheduler.cancel();
            jobRemoved = removeJob(jobIdentifier);
        }
        return jobRemoved ? NavigationCase.OK.toString() : NavigationCase.FAIL.toString();
    }

    private String getImportJobIdentifier() {
        return ImportScheduler.DEFAULT_GROUP + "."  + ImportScheduler.DEFAULT_IMPORT_JOB_ID;
    }

    private String getExportJobIdentifier() {
        return ExportScheduler.DEFAULT_GROUP + "."  + ExportScheduler.DEFAULT_EXPORT_JOB_ID;
    }

    //TODO
    private boolean removeJob(final String jobIdentifier) {
        final List<JobDetail> readonlyJobs = new ArrayList(jobs);
        for (int i = 0; i < readonlyJobs.size(); i++) {
            if (readonlyJobs.get(i).getKey().toString().equals(jobIdentifier)) {
                jobs.remove(i);
                logger.debug("Job removed={}", readonlyJobs.get(i).toString());
                return true;
            }
        }
        return false;
    }
}

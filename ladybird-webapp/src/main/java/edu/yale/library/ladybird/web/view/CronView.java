package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobsList;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobs;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
        final ScheduledJobs scheduledJobs = new ScheduledJobsList(); //TODO
        jobs = scheduledJobs.getJobs();
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
        if (jobIdentifier.equals(ImportScheduler.getImportJobIdentifier())) {
            importJobScheduler.cancel();
            jobRemoved = removeJob(jobIdentifier);
        } else if (jobIdentifier.equals(ExportScheduler.getExportJobIdentifier())) {
            exportScheduler.cancel();
            jobRemoved = removeJob(jobIdentifier);
        }
        return jobRemoved ? NavigationCase.OK.toString() : NavigationCase.FAIL.toString();
    }

    private boolean removeJob(final String jobIdentifier) {
        final ListIterator<JobDetail> iterator = jobs.listIterator();
        while (iterator.hasNext()) {
            JobDetail j = iterator.next();
            if (j.getKey().toString().equals(jobIdentifier)) {
                logger.debug("Job removed={}", jobIdentifier);
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}

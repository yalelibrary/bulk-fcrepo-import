package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.entity.CronBean;
import edu.yale.library.ladybird.kernel.cron.JobsManager;
import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;


@ManagedBean
@RequestScoped
public class CronSchedulerView extends AbstractView {
    private Logger logger = LoggerFactory.getLogger(CronSchedulerView.class);

    private CronBean cronBean = new CronBean();

    @Inject
    private ImportScheduler importScheduler;

    @Inject
    private ExportScheduler exportScheduler;

    @Inject
    JobsManager jobsManager;

    @PostConstruct
    public void init() {
        initFields();
    }

    public String save() {
        try {
            scheduleImportExport();
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Error scheduling", e);
            return NavigationCase.FAIL.toString();
        }
    }

    public CronBean getCronBean() {
        return cronBean;
    }

    public void setCronBean(CronBean cronBean) {
        this.cronBean = cronBean;
    }

    private void scheduleImportExport() throws Exception {
        try {
            if (!isCronScheduled()) {
                importScheduler.scheduleJob(getCronBean().getImportCronExpression());
                exportScheduler.scheduleJob(getCronBean().getExportCronExpression());
            }
        } catch (Exception e) {
           throw e;
        }
    }

    //TODO robust mechanism to ensure that an import/export pair exists
    public boolean isCronScheduled() {
        final List<JobDetail> jobs = jobsManager.getJobs();
        if (!jobs.toString().contains(ImportScheduler.DEFAULT_GROUP) && !jobs.toString().contains(ExportScheduler.DEFAULT_EXPORT_JOB_ID)) {
            //logger.debug("Job dtails={}", jobs);
            return false;

        }
        return true;
    }

}

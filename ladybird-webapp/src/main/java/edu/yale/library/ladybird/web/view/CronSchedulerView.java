package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.cron.scheduler.ExportFileMailerScheduler;
import edu.yale.library.ladybird.engine.cron.scheduler.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.scheduler.ImageConversionScheduler;
import edu.yale.library.ladybird.engine.cron.scheduler.ImportScheduler;
import edu.yale.library.ladybird.entity.CronBean;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobs;
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
    private ExportFileMailerScheduler exportFileMailerScheduler;

    @Inject
    private ImageConversionScheduler imageConversionScheduler;

    @Inject
    ScheduledJobs scheduledJobs;

    @PostConstruct
    public void init() {
        initFields();
    }

    public String save() {
        try {
            scheduleCrons();
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

    private void scheduleCrons() throws Exception {
        try {
            if (!isImportCronScheduled()) {
                importScheduler.scheduleJob(getCronBean().getImportCronExpression());
            }

            if (!isExportCronScheduled()) {
                exportScheduler.scheduleJob(getCronBean().getExportCronExpression());
            }

            //For e-mail file notification:
            if (!isExportFileMailerCronScheduled()) {
                exportFileMailerScheduler.scheduleJob(getCronBean().getFileNotificationCronExpression());
            }

            if (!isImageConversionCronScheduled()) {
                imageConversionScheduler.scheduleJob(getCronBean().getImageConversionExpression());
            }
        } catch (Exception e) {
           throw e;
        }
    }

    //TODO robust mechanism to ensure that an import/export pair exists
    public boolean isCronScheduled() {
        final List<JobDetail> jobs = scheduledJobs.getJobs();
        if (!jobs.toString().contains(ImportScheduler.getDefaultGroup()) || !jobs.toString().contains(ExportScheduler.getDefaultExportJobId())) {
            return false;
        }
        return true;
    }

    //TODO robust mechanism to ensure that an import/export pair exists
    public boolean isImportCronScheduled() {
        final List<JobDetail> jobs = scheduledJobs.getJobs();
        if (!jobs.toString().contains(ImportScheduler.getDefaultGroup())) {
            return false;
        }
        return true;
    }

    //TODO robust mechanism to ensure that an import/export pair exists
    public boolean isExportCronScheduled() {
        final List<JobDetail> jobs = scheduledJobs.getJobs();
        if (!jobs.toString().contains(ExportScheduler.getDefaultExportJobId())) {
            return false;
        }
        return true;
    }

    //TODO robust mechanism to ensure that an import/export pair exists
    public boolean isExportFileMailerCronScheduled() {
        final List<JobDetail> jobs = scheduledJobs.getJobs();
        if (!jobs.toString().contains(ExportFileMailerScheduler.getDefaultJobId())) {
            return false;
        }
        return true;
    }

    public boolean isImageConversionCronScheduled() {
        final List<JobDetail> jobs = scheduledJobs.getJobs();
        if (!jobs.toString().contains(ImageConversionScheduler.getDefaultJobId())) {
            return false;
        }
        return true;
    }

}

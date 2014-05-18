package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.entity.CronBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

/**
 * TODO
 */
@ManagedBean
@ApplicationScoped
public class CronSchedulerView extends AbstractView {
    private Logger logger = LoggerFactory.getLogger(CronSchedulerView.class);

    private CronBean cronBean = new CronBean();

    public boolean cronScheduled = false;

    private static final String IMPORT_JOB_ID = "import_job";
    private static final String IMPORT_JOB_TRIGGER = "trigger";
    private static final String EXPORT_JOB_ID = "export_job";

    @Inject
    private ImportScheduler importScheduler;

    @Inject
    private ExportScheduler exportScheduler;

    @PostConstruct
    public void init() {
        initFields();
    }

    public String save() {
        logger.debug("Executing cron scheduling with bean={}", cronBean.toString());
        scheduleImportExport();
        return NavigationCase.OK.toString();
    }

    public CronBean getCronBean() {
        return cronBean;
    }

    public void setCronBean(CronBean cronBean) {
        this.cronBean = cronBean;
    }

    private void scheduleImportExport() {
        if (!cronScheduled) {
            importScheduler.scheduleJob(IMPORT_JOB_ID, IMPORT_JOB_TRIGGER, getCronBean().getFilePickerCronExpression());
            exportScheduler.scheduleJob(EXPORT_JOB_ID, getCronBean().getExportCronExpression());

            cronScheduled = true;
        }
    }

    public boolean isCronScheduled() {
        return cronScheduled;
    }
}

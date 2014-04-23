package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.kernel.model.Monitor;
import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.FilePickerScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.engine.model.CronSchedulingException;
import edu.yale.library.ladybird.persistence.dao.MonitorDAO;
import org.hibernate.HibernateException;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@ApplicationScoped
@SuppressWarnings("unchecked")
public class MonitorView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private static final String IMPORT_JOB_ID = "import_job";
    private static final String IMPORT_JOB_TRIGGER = "trigger";
    private static final String EXPORT_JOB_ID = "export_job";
    private static final String EXPORT_JOB_TRIGGER = "trigger_export";
    private static final String FILEPICKER_JOB_ID = "pickup_job_";
    private static final String FILEPICKER_JOB_TRIGGER = "trigger";
    private static final String FILEPACKER_JOB_GROUP_ID = "group";

    private List<Monitor> itemList;
    private Monitor monitorItem = new Monitor();

    @Inject
    private MonitorDAO monitorDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = monitorDAO;
    }

    public void save() {
        try {
            logger.debug("Saving import/export pair=" + monitorItem.toString());

            int itemId = dao.save(monitorItem);

            logger.debug("Scheduling file pick, import, export jobs");

            /* FIXME. Done because currently Import/Export Engine notification e-mail is not tied to a particular User.
               It just asks for user e-mail. When it's linked, a drop down should appear, obviating the need for this
               line.*/

            monitorItem.getUser().setEmail(monitorItem.getNotificationEmail());

            // Set off file monitoring for the particular directory (assumes new scheduler per directory):
            FilePickerScheduler filePickerScheduler = new FilePickerScheduler();
            filePickerScheduler.schedulePickJob(FILEPICKER_JOB_ID + itemId, //some uuid?
                    FILEPICKER_JOB_TRIGGER + itemId, FILEPACKER_JOB_GROUP_ID
                    + itemId, getFilePickupCronString(), monitorItem);

            // Run import export cron pair on this directory (associated with user) from now on
            // Set off import cron:
            ImportScheduler importScheduler = new ImportScheduler();
            importScheduler.scheduleJob(IMPORT_JOB_ID, IMPORT_JOB_TRIGGER, getImportCronSchedule());

            // Set off export cron:
            ExportScheduler exportScheduler = new ExportScheduler();
            exportScheduler.scheduleJob(EXPORT_JOB_ID, EXPORT_JOB_TRIGGER, getExportCronSchedule(), monitorItem);
        } catch (CronSchedulingException e) {
            logger.error("Error scheduling import/export job", e); //ignore exception
        } catch (HibernateException h) {
            logger.error("Error saving import/export pair", h); //ignore exception
        }
    }

    private String getFilePickupCronString() {
        return "0/60 * * * * ?";
    }

    private String getExportCronSchedule() {
        return "0/150 * * * * ?";
    }

    private String getImportCronSchedule() {
        return "0/120 * * * * ?";
    }

    public List getItemList() {
        List<Monitor> monitorList = dao.findAll();
        return monitorList;
    }

    public Monitor getMonitorItem() {
        return monitorItem;
    }

    public void setMonitorItem(Monitor monitorItem) {
        this.monitorItem = monitorItem;
    }

    @Override
    public String toString() {
        return monitorItem.toString();
    }

}



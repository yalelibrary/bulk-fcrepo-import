package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.cron.ImportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.FilePickerScheduler;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.engine.CronSchedulingException;
import edu.yale.library.ladybird.persistence.dao.MonitorDAO;
import org.hibernate.HibernateException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

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

    private UploadedFile uploadedFile;
    private String uploadedFileName;
    private InputStream uploadedFileStream;

    @Inject
    private MonitorDAO monitorDAO;

    @Inject
    private FilePickerScheduler filePickerScheduler;

    @Inject
    private ImportScheduler importScheduler;

    @Inject
    private ExportScheduler exportScheduler;

    @PostConstruct
    public void init() {
        initFields();
        dao = monitorDAO;
    }

    public String process() {
        logger.debug("Scheduling import, export jobs. Processing file={}", uploadedFileName);

        try {
            logger.debug("Saving import/export pair=" + monitorItem.toString());

            int itemId = dao.save(monitorItem);

            monitorItem.setDirPath("local");

            monitorItem.getUser().setEmail(monitorItem.getNotificationEmail());

            //Duplicate of FilePickerJob.class

            final SpreadsheetFile file = new SpreadsheetFileBuilder()
                    .setFileName(uploadedFileName)
                    .setAltName(uploadedFileName)
                    .setFileStream(uploadedFileStream)
                    .createSpreadsheetFile();

            final ImportRequestEvent importEvent = new ImportRequestEvent(file, monitorItem);

            logger.debug("Prepared event=" + importEvent.toString());

            ImportEngineQueue.addJob(importEvent);

            logger.debug("Enqueued event=" + importEvent.toString());

            scheduleImportExport(monitorItem);

            return NavigationCase.OK.toString();
        } catch (CronSchedulingException | HibernateException e) {
            logger.error("Error scheduling or saving import/export job", e); //TODO
        }
        return NavigationCase.FAIL.toString();
    }

    public String save() {
        logger.debug("Scheduling file pick, import, export jobs");
        try {
            logger.debug("Saving import/export pair=" + monitorItem.toString());

            int itemId = dao.save(monitorItem);

            //FIXME:
            monitorItem.getUser().setEmail(monitorItem.getNotificationEmail());

            // Set off file monitoring for the particular directory (assumes new scheduler per directory):
            filePickerScheduler.schedulePickJob(FILEPICKER_JOB_ID + itemId, //some uuid?
                    FILEPICKER_JOB_TRIGGER + itemId, FILEPACKER_JOB_GROUP_ID
                    + itemId, getFilePickupCronString(), monitorItem);

            scheduleImportExport(monitorItem);

            return NavigationCase.OK.toString();
        } catch (CronSchedulingException | HibernateException e) {
            logger.error("Error scheduling or saving import/export job", e); //TODO
        }

        return NavigationCase.FAIL.toString();
    }

    private void scheduleImportExport(final Monitor monitorItem) {
        // Set off import cron:
        importScheduler.scheduleJob(IMPORT_JOB_ID, IMPORT_JOB_TRIGGER, getImportCronSchedule());

        // Set off export cron:
        exportScheduler.scheduleJob(EXPORT_JOB_ID, EXPORT_JOB_TRIGGER, getExportCronSchedule(), monitorItem);
    }

    private String getFilePickupCronString() {
        return "0/60 * * * * ?";
    }

    private String getExportCronSchedule() {
        return "0/60 * * * * ?";
    }

    private String getImportCronSchedule() {
        return "0/60 * * * * ?";
    }

    public List getItemList() {
        final List<Monitor> monitorList;
        try {
            monitorList = dao.findAll();
            return monitorList;
        } catch (Exception e) {
            logger.error("Error finding item list={}", e);
            throw e;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
        this.uploadedFileName = uploadedFile.getFileName();
        try {
            uploadedFileStream = uploadedFile.getInputstream();
        } catch (IOException e) {
            logger.error("Input stream null for file={}", event.getFile().getFileName());
        }
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



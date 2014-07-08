package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobNotificationsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportJob implements Job, ExportJob {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Export Job reads content rows from import job tables and writes to a spreadsheet
     * todo find out how to tie user id to file data
     *
     * @param ctx
     * @throws org.quartz.JobExecutionException
     *
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException {

        final ExportEngine exportEngine = new DefaultExportEngine();

        try {
            final long startTime = System.currentTimeMillis();
            logger.trace("Looking for export job.");
            final ImportEntityContext importEntityContext = exportEngine.read();

            logger.debug("[start] export job.");
            logger.trace("Read rows from export engine, list size={}, import job context={}",
                    importEntityContext.getImportJobList().size(), importEntityContext.toString());

            //1. Write to spreadsheet
            final String exportFilePath = getWritePath(exportFile(importEntityContext.getMonitor().getExportPath()));
            logger.debug("Export file path={}", exportFilePath);

            exportEngine.write(importEntityContext.getImportJobList(), exportFilePath);
            //1b. Update imjobs table
            updateImportJobs(importEntityContext.getImportId(), exportFilePath);
            //1c. Create entry in import job notifications table (spreadsheet file will be emailed)
            updateImportJobsNotification(importEntityContext.getImportId(), importEntityContext.getMonitor().getUser().getUserId());

            logger.debug("[end] Completed export job in={}",
                    DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss:SS"));

            //2. Write to object metadata tables
            logger.debug("Writing contents to object metadata tables. Row size={}", importEntityContext.getImportJobList().size());
            ObjectWriter objectWriter = new ObjectWriter(); //TODO
            objectWriter.write(importEntityContext);
            //logger.debug("[end] Wrote to object metadata tables");

            /* Add params as desired */
            final Event exportEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importEntityContext.getImportJobList().size()).createExportCompleteEvent();

            //Post progress
            ExportBus.postEvent(new ExportProgressEvent(exportEvent, importEntityContext.getMonitor().getId())); //TODO consolidate

            logger.debug("Adding export event; notifying user registered for this event instance.");

            sendNotification(exportEvent, Collections.singletonList(importEntityContext.getMonitor().getUser())); //todo

            logger.trace("Added export event to notification queue.");
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void updateImportJobs(int jobId, String exportFilePath) {
        logger.debug("Updating import jobs table with jobId={} exportFilePath={}", jobId, exportFilePath);
        try {
            ImportJobDAO importJobDAO = new ImportJobHibernateDAO(); //TODO
            ImportJob importJob = importJobDAO.findByJobId(jobId).get(0); //TODO error if more than one job found
            importJob.setExportJobFile(exportFilePath); //TODO use either export file path or directory
            importJob.setExportJobDir(exportFilePath);  //TODO
            importJobDAO.saveOrUpdateItem(importJob);
            logger.debug("Updated entity={}", importJob);
            logger.debug("Updated list={}", importJobDAO.findAll()); //TODO remove
        } catch (Exception e) {
            logger.error("Error updating import job"); //TODO throw exception
        }
    }

    private void updateImportJobsNotification(int jobId, int userId) {
        logger.debug("Updating import jobs notifications with jobId={} userId={}", jobId, userId);
        try {
            ImportJobNotificationsDAO dao = new ImportJobNotificationsHibernateDAO(); //TODO
            ImportJobNotifications importJobNotifications = new ImportJobNotifications();
            importJobNotifications.setImportJobId(jobId);
            importJobNotifications.setDateCreated(new Date());
            importJobNotifications.setUserId(userId);
            dao.save(importJobNotifications);
            logger.debug("Saved entity={}", importJobNotifications);
        } catch (Exception e) {
            logger.error("Error updating import job notification"); //TODO throw exception
        }
    }

    private void sendNotification(final Event exportEvent, final List<User> u) {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u));
    }

    private String exportFile(final String folder) {
        return folder + File.separator + "export-results-" + System.currentTimeMillis() + ".xlsx"; //todo
    }

    /**
     * Returns absolute write appended with project
     * @param relativePath
     * @return
     */
    private String getWritePath(final String relativePath) {
        final SettingsDAO settingsDAO = new SettingsHibernateDAO();
        logger.trace("Looking up relative path={}", relativePath);
        if (ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH == null) {
            logger.error("No import root path. Returning relative path as is.");
            return relativePath;
        }

        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH + File.separator + relativePath;
        } else {
            logger.debug("Full path as={}", settings.getValue() + File.separator + relativePath);
            return settings.getValue() + File.separator + relativePath;
        }
    }


}

package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEvent;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportSheet;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldExportOptionsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobNotificationsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserProjectFieldExportOptionsHibernateDAO;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

            if (importEntityContext.getImportJobList().isEmpty()) {
                logger.error("No rows found to export.");
                throw new JobExecutionException("No rows found to export");
            }

            logger.debug("[start] export job.");
            logger.trace("Read rows from export engine, list size={}, import job context={}",
                    importEntityContext.getImportJobList().size(), importEntityContext.toString());

            //1. Write to spreadsheet
            final String exportFilePath = getWritePath(exportFile(importEntityContext.getMonitor().getExportPath()));
            logger.debug("Export file path={}", exportFilePath);


            //1a. Set export fields
            final List<ExportSheet> exportSheets = new ArrayList<>();

            ExportSheet exportSheet = new ExportSheet();
            exportSheet.setTitle("Full Sheet");
            exportSheet.setContents(importEntityContext.getImportJobList());
            exportSheets.add(exportSheet);

            ExportSheet exportSheet2 = getCustomSheet(importEntityContext.getImportJobList(), importEntityContext.getMonitor());
            exportSheets.add(exportSheet2);

            exportEngine.writeSheets(exportSheets, exportFilePath);
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
            final ExportCompleteEvent exportEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importEntityContext.getImportJobList().size()).createExportCompleteEvent();
            exportEvent.setImportId(importEntityContext.getImportId());

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
            logger.error("Error updating import job", e); //TODO throw exception
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
            logger.error("Error updating import job notification", e); //TODO throw exception
        }
    }

    private void sendNotification(final ExportCompleteEvent exportEvent, final List<User> u) {
        //TODO construct message and subject
        String message = "Exported number of rows was" + exportEvent.getRowsProcessed();
        String subject = "Export for job # " + exportEvent.getImportId() + " is complete.";
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u, message, subject));
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
            logger.trace("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH + File.separator + relativePath;
        } else {
            logger.trace("Full path as={}", settings.getValue() + File.separator + relativePath);
            return settings.getValue() + File.separator + relativePath;
        }
    }

    /**
     * Gets custom sheet
     * @param fullList original list
     * @param monitor context data
     * @return an ExportSheet representing purged list
     *
     * TODO test
     */
    private ExportSheet getCustomSheet(List<ImportEntity.Row> fullList, Monitor monitor) {
        final UserProjectFieldExportOptionsDAO dao = new UserProjectFieldExportOptionsHibernateDAO(); //TODO
        final ExportSheet exportSheet = new ExportSheet();
        exportSheet.setTitle("Custom Sheet");

        final int projectId = monitor.getCurrentProject().getProjectId();
        final int userId = monitor.getUser().getUserId();

        List<Integer> columnsToExclude = new ArrayList<>();
        List<Integer> columnsFdidsToExclude = new ArrayList<>();
        List<ImportEntity.Row> newList = new ArrayList<>();

        try {
            logger.debug("Finding custom fields for user={} for project={}", userId, projectId);

            ImportEntity.Row exheadRow = fullList.get(0); // get exhead (show probably use ImportEntityValue somewhere)

            for (int i = 0; i < exheadRow.getColumns().size(); i++) {
                ImportEntity.Column<String> c = exheadRow.getColumns().get(i);

                if (FunctionConstants.isFunction(c.getValue())) {
                    continue;
                }

                int fdid = FieldDefinition.fdidAsInt(c.getValue());
                UserProjectFieldExportOptions u = dao.findByUserAndProjectAndFdid(userId, projectId, fdid);
                if (u == null) { //not found = should not be exported
                    columnsToExclude.add(i);
                    columnsFdidsToExclude.add(fdid);
                }
            }

            logger.debug("Col nums to exclude={}", columnsToExclude.size());

            for (ImportEntity.Row row: fullList) {

                List<ImportEntity.Column> newColumns = new ArrayList<>();

                for (ImportEntity.Column<String> col: row.getColumns()) {
                    if (FunctionConstants.isFunction(col.getField().getName())) {
                        newColumns.add(col);
                        continue;
                    }

                    logger.trace("Eval={}", col.getField());
                    FieldDefinition f = (FieldDefinition) col.getField();
                    int fdid = f.getFdid();

                    if (columnsFdidsToExclude.contains(fdid)) {
                        logger.trace("Excluding={}", fdid);
                    } else {
                        newColumns.add(col);
                    }
                }

                ImportEntity.Row newRow = new ImportEntity().new Row();
                newRow.setColumns(newColumns);
                newList.add(newRow);
            }
        } catch (Exception e) {
            logger.error("Error getting contents for custom sheet", e);
        }
        exportSheet.setContents(newList);
        return exportSheet;
    }

}

package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.ProgressEventListener;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEvent;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ExportSheet;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ObjectMetadataWriter;
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

import static edu.yale.library.ladybird.engine.ExportBus.postEvent;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationHMS;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationWords;
import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportJob implements Job, ExportJob {
    private final Logger logger = getLogger(this.getClass());

    private ImportJobDAO importJobDAO = new ImportJobHibernateDAO();

    private ImportJobNotificationsDAO notificationsDAO = new ImportJobNotificationsHibernateDAO();

    private final SettingsDAO settingsDAO = new SettingsHibernateDAO();

    /**
     * Export Job reads content rows from import job tables and writes to a spreadsheet
     * TODO find out how to tie user id to file data
     *
     * @param ctx org.quartz.JobExecutionContext
     * @throws org.quartz.JobExecutionException
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        final ExportEngine exportEngine = new DefaultExportEngine();

        try {
            logger.trace("[wait] export job.");
            final ImportEntityContext importEntityContext = exportEngine.read();

            if (importEntityContext.getImportJobList().isEmpty()) {
                logger.error("0 rows to export.");
                throw new JobExecutionException("0 rows to export");
            }

            logger.debug("[start] export job={}", importEntityContext.getImportId());

            ExportRequestEvent exportRequestEvent = new ExportRequestEvent();

            //post init
            postEvent(new ProgressEvent(importEntityContext.getMonitor().getId(),
                    exportRequestEvent, ProgressEventListener.JobStatus.IN_PROGRESS));

            /**
             * 1. a. Write to spreadsheet, b. update import_jobs, c. send file
             */
            final long timeInXlsWriting = System.currentTimeMillis();
            final String exportFilePath = getWritePath(exportFile(importEntityContext.getMonitor().getExportPath()));
            logger.debug("Export file path={}", exportFilePath);
            final List<ExportSheet> exportSheets = new ArrayList<>();
            final ExportSheet sheet1 = new ExportSheet();
            sheet1.setTitle("Full Sheet");
            sheet1.setContents(importEntityContext.getImportJobList());
            exportSheets.add(sheet1);
            final ExportSheet sheet2 = getCustomSheet(importEntityContext.getImportJobList(), importEntityContext.getMonitor());
            exportSheets.add(sheet2);
            exportEngine.writeSheets(exportSheets, exportFilePath);

            //1b. Update imjobs table
            updateImportJobs(importEntityContext.getImportId(), exportFilePath);
            //1c. Create entry in import job notifications table (to enable spreadsheet file mailing etc)
            updateImportJobsNotification(importEntityContext.getImportId(), importEntityContext.getMonitor().getUser().getUserId());

            long elapsed = System.currentTimeMillis() - timeInXlsWriting;
            logger.debug("[end] Completed spreadsheet writing in={}",
                    DurationFormatUtils.formatDuration(elapsed, "HH:mm:ss:SS"));

            /**
             * 2. Write to object metadata tables, post ExportCompleteEvent and progress
             */
            logger.debug("Writing to object metadata tables. size={}", importEntityContext.getImportJobList().size());
            ObjectMetadataWriter objectMetadataWriter = new ObjectMetadataWriter(); //TODO
            long timeInObjWriter = System.currentTimeMillis();
            objectMetadataWriter.write(importEntityContext);
            logger.debug("[end] Wrote to object metadata tables in={}", formatDurationHMS(System.currentTimeMillis() - timeInObjWriter));
            final ExportCompleteEvent exportCompEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importEntityContext.getImportJobList().size()).setTime(elapsed).createExportCompleteEvent();
            exportCompEvent.setImportId(importEntityContext.getImportId());
            postEvent(new ProgressEvent(importEntityContext.getMonitor().getId(), exportCompEvent, ProgressEventListener.JobStatus.COMPLETE));
            logger.debug("Notifying user registered.");
            sendNotification(exportCompEvent, Collections.singletonList(importEntityContext.getMonitor().getUser()));
            logger.trace("Added export event to notification queue.");
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void updateImportJobs(final int jobId, final String exportFilePath) {
        logger.debug("Updating import_jobs table with jobId={} exportFilePath={}", jobId, exportFilePath);
        try {
            ImportJob importJob = importJobDAO.findByJobId(jobId).get(0); //TODO error if more than one job found
            importJob.setExportJobFile(exportFilePath);
            importJob.setExportJobDir(exportFilePath);
            importJobDAO.saveOrUpdateItem(importJob);
        } catch (Exception e) {
            logger.error("Error updating import job", e); //TODO throw exception
        }
    }

    private void updateImportJobsNotification(final int jobId, final int userId) {
        logger.debug("Updating import jobs notifications with jobId={} userId={}", jobId, userId);
        try {
            ImportJobNotifications notice = new ImportJobNotifications();
            notice.setImportJobId(jobId);
            notice.setDateCreated(new Date());
            notice.setUserId(userId);
            notificationsDAO.save(notice);
            logger.debug("Saved entity={}", notice);
        } catch (Exception e) {
            logger.error("Error updating import job notification", e); //TODO throw exception
        }
    }

    private void sendNotification(final ExportCompleteEvent exportEvent, final List<User> u) {
        String message = "Rows exported:" + exportEvent.getRowsProcessed();
        message += ", Time:" + formatDurationWords(exportEvent.getTime(), true, true);
        String subject = "Export complete for job # " + exportEvent.getImportId();
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u, message, subject));
    }

    private String exportFile(final String folder) {
        return folder + File.separator + "export-results-" + System.currentTimeMillis() + ".xlsx"; //todo
    }

    /**
     * Returns absolute write appended with project
     * @param relativePath file path
     * @return full path
     */
    private String getWritePath(final String relativePath) {
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
            logger.debug("Finding custom fields for user={} for projectId={}", userId, projectId);

            ImportEntity.Row exheadRow = fullList.get(0); // get exhead (show probably use ImportEntityValue somewhere)

            for (int i = 0; i < exheadRow.getColumns().size(); i++) {
                ImportEntity.Column<String> c = exheadRow.getColumns().get(i);

                if (FunctionConstants.isFunction(c.getValue())) {
                    continue;
                }

                int fdid = FieldDefinition.fdidAsInt(c.getValue());
                UserProjectFieldExportOptions userOptions = dao.findByUserAndProjectAndFdid(userId, projectId, fdid);

                if (userOptions == null) { //not found = should not be exported
                    columnsToExclude.add(i);
                    columnsFdidsToExclude.add(fdid);
                }
            }

            logger.debug("Num. of columns to exclude={}", columnsToExclude.size());

            for (ImportEntity.Row row: fullList) {
                final List<ImportEntity.Column> newColumns = new ArrayList<>();

                for (final ImportEntity.Column<String> col: row.getColumns()) {
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

package edu.yale.library.ladybird.engine.cron.job;


import edu.yale.library.ladybird.engine.JobStatus;
import edu.yale.library.ladybird.engine.cron.ExportJob;
import edu.yale.library.ladybird.engine.cron.ProgressEvent;
import edu.yale.library.ladybird.engine.cron.queue.ExportWriterQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEvent;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ExportSheet;
import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.entity.JobRequest;
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

import static edu.yale.library.ladybird.engine.EventBus.post;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationWords;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;
import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportJob implements Job, ExportJob {

    private final Logger logger = getLogger(this.getClass());

    private ImportJobDAO importJobDAO = new ImportJobHibernateDAO();

    private ImportJobNotificationsDAO notificationsDAO = new ImportJobNotificationsHibernateDAO();

    private final SettingsDAO settingsDAO = new SettingsHibernateDAO();

    private final ExportEngine exportEngine = new DefaultExportEngine();

    /**
     * Export Job reads content rows from import job tables and writes to a spreadsheet
     *
     * TODO find out how to tie user id to file data
     *
     * @param ctx org.quartz.JobExecutionContext
     * @throws org.quartz.JobExecutionException
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException {

        try {
            final ImportContext importContext = ExportWriterQueue.getJob();

            if (importContext == null) {
                return;
            }

            logger.debug("Read from export engine queue, import context size={}",
                    importContext.getImportRowsList().size());

            if (importContext.getImportRowsList().isEmpty()) {
                logger.error("0 rows to export.");
                throw new JobExecutionException("0 rows to export");
            }

            ExportRequestEvent exportRequestEvent = new ExportRequestEvent();

            //post init
            final int jobRequestId = importContext.getJobRequest().getId();
            post(new ProgressEvent(jobRequestId,
                    exportRequestEvent, JobStatus.INIT));

            /**
             * Write to spreadsheet, b. update import_jobs, c. send file
             */
            logger.debug("Writing sheet for export job={}", importContext.getImportId());

            final long timeInXlsWriting = System.currentTimeMillis();
            final String exportFilePath = getWritePath(exportFile(importContext.getJobRequest().getExportPath()));
            final List<ExportSheet> exportSheets = new ArrayList<>();
            final ExportSheet sheet1 = new ExportSheet();
            sheet1.setTitle("Full Sheet");
            sheet1.setContents(importContext.getImportRowsList());
            exportSheets.add(sheet1);
            final ExportSheet sheet2 = getCustomSheet(importContext.getImportRowsList(), importContext.getJobRequest());
            exportSheets.add(sheet2);
            exportEngine.writeSheets(exportSheets, exportFilePath);

            //b. Update imjobs table
            updateImportJobs(importContext.getImportId(), exportFilePath);
            //c. Create entry in import job notifications table (to enable spreadsheet file mailing etc)
            updateImportJobsNotification(importContext.getImportId(), importContext.getJobRequest().getUser().getUserId());

            long elapsedInXls = System.currentTimeMillis() - timeInXlsWriting;
            logger.debug("[end] Completed spreadsheet writing in={} for jobRequestId={}",
                    formatDuration(elapsedInXls, "HH:mm:ss:SS"),
                    jobRequestId);

            final ExportCompleteEvent exportCompEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importContext.getImportRowsList().size()).setTime(elapsedInXls)
                    .createExportCompleteEvent();

            exportCompEvent.setImportId(importContext.getImportId());
            post(new ProgressEvent(jobRequestId, exportCompEvent, JobStatus.DONE));

            sendNotification(exportCompEvent, Collections.singletonList(importContext.getJobRequest().getUser()));
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final ExportCompleteEvent exportEvent, final List<User> u) {
        String subject = "Job # " + exportEvent.getImportId() + " XLS write complete";
        String message = "Metadata  written to a spreadsheet with rows:"
                + exportEvent.getRowsProcessed();
        message += ", Xls write time:" + formatDurationWords(exportEvent.getTime(), true, true);
        NotificationEventQueue.addEvent(new NotificationEventQueue()
                .new NotificationItem(exportEvent, u, message, subject));
    }

    private void updateImportJobs(final int jobId, final String exportFilePath) {
        logger.debug("Updating import_jobs table for jobId={}", jobId);
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
        logger.trace("Updating import job notifications for jobId={}", jobId);
        try {
            ImportJobNotifications notice = new ImportJobNotifications();
            notice.setImportJobId(jobId);
            notice.setDateCreated(new Date());
            notice.setUserId(userId);
            notificationsDAO.save(notice);
        } catch (Exception e) {
            logger.error("Error updating import job notification", e); //TODO throw exception
        }
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
        if (ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH == null) {
            logger.error("No import root path. Returning relative path as is.");
            return relativePath;
        }

        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.trace("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH + File.separator + relativePath;
        } else {
            return settings.getValue() + File.separator + relativePath;
        }
    }

    /**
     * Gets custom sheet
     * @param fullList original list
     * @param jobRequest context data
     * @return an ExportSheet representing purged list
     *
     * TODO test
     */
    private ExportSheet getCustomSheet(List<Import.Row> fullList, JobRequest jobRequest) {
        final UserProjectFieldExportOptionsDAO dao = new UserProjectFieldExportOptionsHibernateDAO(); //TODO
        final ExportSheet exportSheet = new ExportSheet();
        exportSheet.setTitle("Custom Sheet");

        final int projectId = jobRequest.getCurrentProject().getProjectId();
        final int userId = jobRequest.getUser().getUserId();

        List<Integer> columnsToExclude = new ArrayList<>();
        List<Integer> columnsFdidsToExclude = new ArrayList<>();
        List<Import.Row> newList = new ArrayList<>();

        try {
            logger.trace("Finding custom fields for job={}", jobRequest.getId());

            Import.Row exheadRow = fullList.get(0); // get exhead (show probably use ImportEntityValue somewhere)

            for (int i = 0; i < exheadRow.getColumns().size(); i++) {
                Import.Column<String> c = exheadRow.getColumns().get(i);

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

            logger.debug("Num. of columns to exclude={} for jobRequest={}", columnsToExclude.size(), jobRequest.getId());

            for (Import.Row row: fullList) {
                final List<Import.Column> newColumns = new ArrayList<>();

                for (final Import.Column<String> col: row.getColumns()) {
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

                Import.Row newRow = new Import().new Row();
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

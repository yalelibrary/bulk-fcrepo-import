package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;
import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
            final ImportJobCtx importJobCtx = exportEngine.read();

            logger.debug("[start] export job.");
            logger.debug("Read rows from export engine, list size={}, import job context={}",
                    importJobCtx.getImportJobList().size(), importJobCtx.toString());


            //1. Write to spreadsheet
            logger.debug("Writing content rows to spreadsheet. . .");

            exportEngine.write(importJobCtx.getImportJobList(), tmpFile(importJobCtx.getMonitor().getExportPath()));

            logger.debug("[end] Completed export job in={}",
                    DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss:SS"));

            //2. Write to object metadata tables
            logger.debug("Writing contents to object metadata tables. Row size={}", importJobCtx.getImportJobList().size());
            ObjectWriter objectWriter = new ObjectWriter(); //TODO
            objectWriter.write(importJobCtx);
            //logger.debug("[end] Wrote to object metadata tables");

            /* Add params as desired */
            final Event exportEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importJobCtx.getImportJobList().size()).createExportCompleteEvent();

            //Post progress
            ExportBus.postEvent(new ExportProgressEvent(exportEvent, importJobCtx.getMonitor().getId())); //TODO consolidate

            logger.debug("Adding export event; notifying user registered for this event instance.");

            sendNotification(exportEvent, Collections.singletonList(importJobCtx.getMonitor().getUser())); //todo

            logger.trace("Added export event to notification queue.");
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final Event exportEvent, final List<User> u) {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u));
    }

    private String tmpFile(final String folder) {
        return folder + File.separator + "export-results-" + System.currentTimeMillis() + ".xlsx"; //todo
    }

}

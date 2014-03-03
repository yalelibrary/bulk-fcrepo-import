package edu.yale.library.engine.cron;


import edu.yale.library.TimeUtils;
import edu.yale.library.beans.User;
import edu.yale.library.engine.exports.ExportRequestEvent;
import edu.yale.library.engine.imports.ImportEngine;
import edu.yale.library.engine.imports.ImportRequestEvent;
import edu.yale.library.engine.imports.SpreadsheetFile;
import edu.yale.library.engine.imports.ImportEntity;
import edu.yale.library.engine.imports.DefaultImportEngine;
import edu.yale.library.engine.imports.ImportCompleteEventBuilder;
import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ImportReaderValidationException;
import edu.yale.library.engine.model.ImportEngineException;
import edu.yale.library.engine.model.ReadMode;
import edu.yale.library.events.Event;
import edu.yale.library.events.NotificationEventQueue;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ImportJob implements Job {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Execute the full cycle and notify the list of users.
     * todo find out how to tie user id to file data
     *
     * @param arg0
     * @throws JobExecutionException
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        final long startTime = System.currentTimeMillis();
        final ImportRequestEvent importRequestedEvent = ImportEngineQueue.getJob();
        final SpreadsheetFile file = importRequestedEvent.getSpreadsheetFile();

        logger.debug("[start] import job. File name={}", file);

        try {
            final ImportEngine importEngine = new DefaultImportEngine();
            final List<ImportEntity.Row>  list = importEngine.read(file, ReadMode.FULL, new DefaultFieldDataValidator());

            logger.debug("Read rows. list size=" + list.size());

            final int imid = importEngine.write(list);

            logger.debug("[end] Completed import job in " + TimeUtils.elapsedMilli(startTime));

            /* Add params as desired */
            final Event importEvent = new ImportCompleteEventBuilder().
                    setRowsProcessed(list.size()).createImportDoneEvent();

            logger.debug("Adding import event and notifying all registered users");

            sendNotification(importEvent, Collections.singletonList(importRequestedEvent.getMonitor().getUser()));

            logger.debug("Added import event to notification queue");

            //Note: This needs to be re-visited per logic requirement
            /* Add request for export */
            final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
            ExportEngineQueue.addJob(exportEvent);

            logger.debug("Added event=" + exportEvent.toString());
        } catch (ImportReaderValidationException e) {
            logger.error("validation exception", e);
            throw new ImportEngineException(e);
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(Event importEvent, List<User> u) {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(importEvent, u));
    }

}

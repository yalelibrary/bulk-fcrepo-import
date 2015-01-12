package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.exports.ExportCompleteEvent;
import edu.yale.library.ladybird.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ObjectMetadataWriter;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

//import static edu.yale.library.ladybird.engine.EventBus.post;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationWords;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationHMS;

import static org.slf4j.LoggerFactory.getLogger;


//TODO schedule
public class DefaultObjectMetadataWriterJob implements Job, ObjectMetataWriterJob {

    private final Logger logger = getLogger(this.getClass());

    private final ObjectMetadataWriter objectMetadataWriter = new ObjectMetadataWriter();

    //TODO post progress. currently sends only notification (with the wrong event)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            final ImportContext importContext = ObjectMetadataWriterQueue.getJob();

            if (importContext == null) {
                return;
            }

            logger.debug("Got polled job, import context size={}", importContext.getImportRowsList().size());

            if (importContext.getImportRowsList().isEmpty()) {
                logger.error("0 rows to export.");
                throw new JobExecutionException("0 rows to write");
            }

            logger.debug("Writing to object metadata tables for importId={}", importContext.getImportId());

            final long timeInObjWriter = current();
            objectMetadataWriter.write(importContext);

            final long elapsedInObjWriter = current() - timeInObjWriter;
            final int jobRequestId = importContext.getJobRequest().getId();

            logger.debug("[end] Wrote to object metadata tables in={} for jobRequestId={}",
                    formatDurationHMS(elapsedInObjWriter), jobRequestId);

            //TODO get some other event. Not exportComplete
            final ExportCompleteEvent exportCompEvent = new ExportCompleteEventBuilder()
                    .setRowsProcessed(importContext.getImportRowsList().size()).setTime(elapsedInObjWriter).createExportCompleteEvent();
            exportCompEvent.setImportId(importContext.getImportId());

            /*
            //TODO should have the correct event, and init block
            post(new ProgressEvent(jobRequestId, exportCompEvent, JobStatus.DONE));
            */

            sendNotification(exportCompEvent, Collections.singletonList(importContext.getJobRequest().getUser()));
        } catch (Exception e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final ExportCompleteEvent exportEvent, final List<User> u) {
        String message = "Rows metadata written:" + exportEvent.getRowsProcessed();
        message += ", Time:" + formatDurationWords(exportEvent.getTime(), true, true);
        String subject = "Export complete for job # " + exportEvent.getImportId();
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u, message, subject));
    }

    private static final long current() {
        return System.currentTimeMillis();
    }
}

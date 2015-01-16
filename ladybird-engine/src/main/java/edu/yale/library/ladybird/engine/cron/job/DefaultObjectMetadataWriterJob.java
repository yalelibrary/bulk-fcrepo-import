package edu.yale.library.ladybird.engine.cron.job;

import edu.yale.library.ladybird.engine.cron.ObjectMetataWriterJob;
import edu.yale.library.ladybird.engine.cron.queue.ObjectMetadataWriterQueue;
import edu.yale.library.ladybird.engine.exports.ObjectMetadataWriteCompleteEvent;
import edu.yale.library.ladybird.engine.exports.ObjectMetadataWriteCompleteEventBuilder;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ObjectMetadataWriter;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.notificaiton.NotificationEventQueue;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationWords;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationHMS;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultObjectMetadataWriterJob implements Job, ObjectMetataWriterJob {

    private final Logger logger = getLogger(this.getClass());

    private final ObjectMetadataWriter objectMetadataWriter = new ObjectMetadataWriter();

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

            final ObjectMetadataWriteCompleteEvent objEvent = new ObjectMetadataWriteCompleteEventBuilder()
                    .setRowsProcessed(importContext.getImportRowsList().size())
                    .setTime(elapsedInObjWriter).createObjectMetadataWriteCompleteEvent();
            objEvent.setImportId(importContext.getImportId());

            sendNotification(objEvent, Collections.singletonList(importContext.getJobRequest().getUser()));
        } catch (Exception e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final ObjectMetadataWriteCompleteEvent objectMetaEvent, final List<User> u) {
        String subject = "Job # " + objectMetaEvent.getImportId() + " metadata population to db complete";
        String message = "Metadata written to tables for spreadsheet rows:"
                + objectMetaEvent.getRowsProcessed();
        message += ", Time:" + formatDurationWords(objectMetaEvent.getTime(), true, true);
        NotificationEventQueue.addEvent(new NotificationEventQueue()
                .new NotificationItem(objectMetaEvent, u, message, subject));
    }

    private static final long current() {
        return System.currentTimeMillis();
    }
}

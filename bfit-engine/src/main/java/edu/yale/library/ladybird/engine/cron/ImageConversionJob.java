package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.JobStatus;
import edu.yale.library.ladybird.engine.cron.queue.ImportImageConversionQueue;
import edu.yale.library.ladybird.engine.imports.ImageConversionRequestEvent;
import edu.yale.library.ladybird.engine.imports.ImageFunctionProcessor;
import edu.yale.library.ladybird.engine.imports.ImageProcessingEvent;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.notificaiton.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static edu.yale.library.ladybird.engine.EventBus.post;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImageConversionJob implements Job {

    private Logger logger = getLogger(this.getClass());

    private SettingsDAO settingsDAO = new SettingsHibernateDAO();

    private ImportJobDAO importJobDAO = new ImportJobHibernateDAO();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.trace("Executing image conversion job");

        final ImageConversionRequestEvent importReqEvent = ImportImageConversionQueue.getJob();

        if (importReqEvent == null) {
            logger.trace("No image conversion job to process");
            return;
        }

        logger.debug("Got polled jobId={} with exportDir={}", importReqEvent.getImportId()
                , importReqEvent.getExportDirPath());

        final long timeInConversion = System.currentTimeMillis();
        final ImageFunctionProcessor imageFunctionProcessor =
                getCtxMediaFunctionProcessor(importReqEvent.getExportDirPath());

        // TODO
        final int importId = importReqEvent.getImportId();
        List<edu.yale.library.ladybird.entity.ImportJob> importJobs = importJobDAO.findByJobId(importId);
        int requestId = -1;

        if (importJobs.isEmpty()) {
            logger.debug("Cannot find fire event: corresponding job not found");
        } else {
            requestId = importJobs.get(0).getRequestId();
        }

        // Post init
        ImageProcessingEvent mediaEventInit = new ImageProcessingEvent();
        mediaEventInit.setImportId(requestId);
        ProgressEvent progressEvent = new ProgressEvent(requestId, mediaEventInit,
                JobStatus.INIT);
        post(progressEvent);

        try {
            imageFunctionProcessor.convert(importReqEvent.getImportId(), importReqEvent.getImportValue());
            final long elapsed = System.currentTimeMillis() - timeInConversion;

            ImageProcessingEvent mediaCompleteEvent = new ImageProcessingEvent();
            mediaCompleteEvent.setDuration(elapsed);
            mediaCompleteEvent.setImportId(requestId);
            //TODO check it matches up:
            mediaCompleteEvent.setConversions(importReqEvent.getImportValue().getContentRows().size());

            // Post completion
            post(new ProgressEvent(requestId, mediaCompleteEvent, JobStatus.DONE));
        } catch (IOException e) {
            logger.error("Error executing job", e);
        }
        logger.debug("Done image conversion job for import job request={}", requestId);
    }

    //TODO
    private void sendNotification(final ImageProcessingEvent importEvent, final List<User> userList) {
        String message = "Media processed: " + importEvent.getConversions();
        message += ",Time: " + DurationFormatUtils.formatDurationWords(importEvent.getDuration(), true, true);
        String subject = "Import complete for job #" + importEvent.getImportId();
        NotificationEventQueue.addEvent(new NotificationEventQueue()
                .new NotificationItem(importEvent, userList, message, subject));
    }

    /**
     * Returns a ImageFunctionProcessor if db state is found
     */
    private ImageFunctionProcessor getCtxMediaFunctionProcessor(final String path) {
        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return new ImageFunctionProcessor(ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH, path);
        }

        final String rootPath = settings.getValue();
        return new ImageFunctionProcessor(rootPath, path);
    }
}

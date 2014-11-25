package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.imports.ImageConversionRequestEvent;
import edu.yale.library.ladybird.engine.imports.ImportCompleteEvent;
import edu.yale.library.ladybird.engine.imports.MediaFunctionProcessor;
import edu.yale.library.ladybird.engine.imports.MediaProcessingCompleteEvent;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Converts image
 */
public class ImageConversionJob implements Job {

    private Logger logger = getLogger(this.getClass());

    private SettingsDAO settingsDAO = new SettingsHibernateDAO();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.trace("Executing image conversion job");

        final ImageConversionRequestEvent importReqEvent = ImportImageConversionQueue.getJob();

        if (importReqEvent == null) {
            logger.trace("No image conversion job to process");
            return;
        }

        logger.debug("Got polled jobId={} with exportDir={}", importReqEvent.getImportId(), importReqEvent.getExportDirPath());

        final long timeInConversion = System.currentTimeMillis();

        final MediaFunctionProcessor mediaFunctionProcessor =
                getCtxMediaFunctionProcessor(importReqEvent.getExportDirPath());
        try {

            mediaFunctionProcessor.convert(importReqEvent.getImportId(), importReqEvent.getImportEntityValue());
            final long elapsed = System.currentTimeMillis() - timeInConversion;

            MediaProcessingCompleteEvent mediaProcessingCompleteEvent = new MediaProcessingCompleteEvent();
            mediaProcessingCompleteEvent.setDuration(elapsed);
            mediaProcessingCompleteEvent.setImportId(importReqEvent.getImportId());
            //TODO check it matches up:
            mediaProcessingCompleteEvent.setConversions(importReqEvent.getImportEntityValue().getContentRows().size());

            ExportBus.postEvent(mediaProcessingCompleteEvent);
            //TODO notify user of import conversion job
        } catch (IOException e) {
            logger.error("Error executing job", e);
        }
        logger.debug("Done image conversion job");
    }

    //TODO
    private void sendNotification(final MediaProcessingCompleteEvent importEvent, final List<User> userList) {
        String message = "Media processed: " + importEvent.getConversions();
        message += ",Time: " + DurationFormatUtils.formatDurationWords(importEvent.getDuration(), true, true);
        String subject = "Import complete for job #" + importEvent.getImportId();
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(importEvent, userList, message, subject));
    }

    /**
     * Returns a MediaFunctionProcessor if db state is found
     */
    private MediaFunctionProcessor getCtxMediaFunctionProcessor(final String path) {
        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return new MediaFunctionProcessor(ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH, path);
        }

        final String rootPath = settings.getValue();
        return new MediaFunctionProcessor(rootPath, path);
    }
}

package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.imports.ImageConversionRequestEvent;
import edu.yale.library.ladybird.engine.imports.MediaFunctionProcessor;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;

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

        final MediaFunctionProcessor mediaFunctionProcessor =
                getCtxMediaFunctionProcessor(importReqEvent.getExportDirPath());
        try {
            mediaFunctionProcessor.process(importReqEvent.getImportId(), importReqEvent.getImportEntityValue());
        } catch (IOException e) {
            logger.error("Error executing job", e);
        }
        logger.debug("Done image conversion job");
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

package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.imports.ImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportReaderValidationException;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.MediaFunctionProcessor;
import edu.yale.library.ladybird.engine.imports.ReadMode;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.ImportSource;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDAO;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultImportJob implements Job, ImportJob {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Execute the full cycle and notify the list of users.
     *
     * @param arg0 JobExecution
     * @throws JobExecutionException
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        final long startTime = System.currentTimeMillis();
        final ImportRequestEvent importRequestedEvent = ImportEngineQueue.getJob();
        final SpreadsheetFile spreadsheetFile = importRequestedEvent.getSpreadsheetFile();

        logger.debug("[start] import job. File name={}", spreadsheetFile);

        try {
            final int userId = importRequestedEvent.getMonitor().getUser().getUserId();
            final int projectId = importRequestedEvent.getMonitor().getCurrentProject().getProjectId();

            final ImportEngine importEngine = new DefaultImportEngine(userId, projectId);

            final DefaultFieldDataValidator fieldDataValidator = new DefaultFieldDataValidator();
            final List<ImportEntity.Row>  rowList = importEngine.read(spreadsheetFile, ReadMode.FULL, fieldDataValidator);

            logger.debug("Read rows. list size={}", rowList.size());

            //TODO provide
            final OaiProvider provider = getCtxOaiProvider();
            importEngine.setOaiProvider(provider);
            //passes relative path for each import job. This is provided by the user on each run. The root path is set application wide.
            final MediaFunctionProcessor mediaFunctionProcessor = getCtxMediaFunctionProcessor(importRequestedEvent.getMonitor().getExportPath());
            importEngine.setMediaFunctionProcessor(mediaFunctionProcessor);
            importEngine.setImportSourceProcessor(new ImportSourceProcessor());

            logger.debug("Writing to import table(s)");

            final int imid = importEngine.write(rowList, spreadsheetFile);

            logger.debug("[end] Completed import (writing) job in {}", DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss:SS"));

            final Event importEvent = new ImportCompleteEventBuilder().setRowsProcessed(rowList.size()).createImportDoneEvent();

            //Post progress
            ExportBus.postEvent(new ExportProgressEvent(importEvent, importRequestedEvent.getMonitor().getId())); //TODO consolidate

            sendNotification(importEvent, Collections.singletonList(importRequestedEvent.getMonitor().getUser()));

            logger.debug("Added import event to notification queue");

            /* Add request for export */  //Note: This needs to be re-visited per logic requirement
            final ExportRequestEvent exportEvent = new ExportRequestEvent(imid, importRequestedEvent.getMonitor());
            ExportEngineQueue.addJob(exportEvent);

            logger.debug("Added event to ExportEngineQueue=" + exportEvent.toString());
        } catch (ImportReaderValidationException e) {
            logger.error("validation exception", e);
            throw new ImportEngineException(e);
        } catch (IOException e) {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        } catch (Exception e) {
            logger.error("General exception", e);
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final Event importEvent, final List<User> userList) {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(importEvent, userList));
    }

    /**
     * Returns OAI provider. Subject to removal (when oai provider is avaialable via some sort of context).
     * Note: returns the 1st provider with status active.
     * @return
     */
    private OaiProvider getCtxOaiProvider() {
        final ImportSourceDAO importSourceDAO = new ImportSourceHibernateDAO();
        final List<ImportSource> importSourceList = importSourceDAO.findAll();

        for (ImportSource importSource: importSourceList) {
            if (importSource.isActive()) {
                return  new OaiProvider("id", importSource.getUrl(), importSource.getGetPrefix());
            }
        }
        return null;
    }

    /** Returns a MediaFunctionProcessor if db state is found */
    private MediaFunctionProcessor getCtxMediaFunctionProcessor(final String path) {
        SettingsDAO settingsDAO = new SettingsHibernateDAO();
        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return new MediaFunctionProcessor(ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH, path);
        }
        final String rootPath = settings.getValue();
        return new MediaFunctionProcessor(rootPath, path);
    }

}

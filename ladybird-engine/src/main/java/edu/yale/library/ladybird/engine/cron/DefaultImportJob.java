package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.kernel.TimeUtils;
import edu.yale.library.ladybird.kernel.beans.ImportSource;
import edu.yale.library.ladybird.kernel.beans.User;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.imports.ImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportCompleteEventBuilder;
import edu.yale.library.ladybird.engine.model.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.model.ImportReaderValidationException;
import edu.yale.library.ladybird.engine.model.ImportEngineException;
import edu.yale.library.ladybird.engine.model.ReadMode;
import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.NotificationEventQueue;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceHibernateDAO;
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
        final SpreadsheetFile file = importRequestedEvent.getSpreadsheetFile();

        logger.debug("[start] import job. File name={}", file);

        try {
            final ImportEngine importEngine = new DefaultImportEngine();
            final DefaultFieldDataValidator fieldDataValidator = new DefaultFieldDataValidator();
            final List<ImportEntity.Row>  rowList = importEngine.read(file, ReadMode.FULL, fieldDataValidator);

            logger.debug("Read rows. list size={}", rowList.size());

            //TODO provide
            final OaiProvider provider = getCtxOaiProvider();
            importEngine.setOaiProvider(provider);
            logger.debug("Set OAI Provider={}", provider);

            logger.debug("Writing to import table(s)");

            final int imid = importEngine.write(rowList);

            logger.debug("[end] Completed import job in {}", TimeUtils.elapsedMilli(startTime));

            /* Add params as desired */
            final Event importEvent = new ImportCompleteEventBuilder().
                    setRowsProcessed(rowList.size()).createImportDoneEvent();

            logger.debug("Adding import event and notifying all registered users");

            sendNotification(importEvent, Collections.singletonList(importRequestedEvent.getMonitor().getUser()));

            logger.debug("Added import event to notification queue");

            /* Add request for export */  //Note: This needs to be re-visited per logic requirement
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

    private void sendNotification(final Event importEvent, final List<User> userList) {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(importEvent, userList));
    }

    /**
     * Returns OAI provider. Subject to removal.
     * Note: returns the 1st provider with status active.
     * @return
     */
    private OaiProvider getCtxOaiProvider() {
        //Note: Full list is importted. This will be removed when some sort of ctx is available.
        final ImportSourceDAO importSourceDAO = new ImportSourceHibernateDAO();
        final List<ImportSource> importSourceList = importSourceDAO.findAll();

        for (ImportSource importSource: importSourceList) {
            if (importSource.isActive()) {
                return  new OaiProvider("id",
                        importSource.getUrl(),
                        importSource.getGetPrefix());
            }
        }
        return null;
    }

}

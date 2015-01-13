package edu.yale.library.ladybird.engine.cron.job;

import edu.yale.library.ladybird.engine.cron.queue.ExportWriterQueue;
import edu.yale.library.ladybird.engine.cron.queue.ObjectMetadataWriterQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;


public class DefaultImportContextJob implements Job {

    private final Logger logger = getLogger(this.getClass());

    private final ExportEngine exportEngine = new DefaultExportEngine();

    /**
     * Reads importcontxt and puts it into exportQ and objectWriterQ
     *
     * @param ctx org.quartz.JobExecutionContext
     * @throws org.quartz.JobExecutionException
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            final ImportContext importContext = exportEngine.read();

            if (importContext == null) {
                return;
            }

            logger.debug("Read from export engine, size={}", importContext.getImportRowsList().size());

            if (importContext.getImportRowsList().isEmpty()) {
                logger.error("0 rows to export.");
                throw new JobExecutionException("0 rows to export");
            }

            logger.debug("putting import context into qs");

            ObjectMetadataWriterQueue.addJob(importContext);
            ExportWriterQueue.addJob(importContext);
        } catch (Exception e) { //TODO
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

}

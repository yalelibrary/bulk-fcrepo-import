package edu.yale.library.engine.cron;


import edu.yale.library.engine.imports.DefaultImportEngine;
import edu.yale.library.engine.imports.ImportEngine;
import edu.yale.library.engine.imports.SpreadsheetFile;
import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ReadMode;
import edu.yale.library.engine.model.UnknownFunctionException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ImportJob implements Job
{
    private final Logger logger = getLogger(this.getClass());

    public void execute(JobExecutionContext arg0) throws JobExecutionException
    {
        long startTime = System.currentTimeMillis();

        SpreadsheetFile latestSpreadsheet = ImportSpreadSheetQueue.getJob();

        logger.debug("[start] import job. File name={}", latestSpreadsheet.getFileName());

        ImportEngine importEngine = new DefaultImportEngine();

        try
        {
            logger.debug("Reading spreadsheet rows");

            List list = importEngine.read(latestSpreadsheet, ReadMode.FULL, new DefaultFieldDataValidator());
            logger.debug("Read rows. list size=" + list.size());
            logger.debug("Writing");

            importEngine.write(list); //TODO
            logger.debug("Wrote spreadseet rows");
        }
        catch (UnknownFunctionException e)
        {
            e.printStackTrace(); //TODO
        }

        logger.debug("[end] Completed import job in " + String.valueOf(System.currentTimeMillis() - startTime));
    }
}

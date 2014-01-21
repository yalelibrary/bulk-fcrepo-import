package edu.yale.library.engine;


import static org.slf4j.LoggerFactory.getLogger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class DefaultImportJob implements Job
{
    private final Logger logger = getLogger(this.getClass());

    public void execute(JobExecutionContext arg0) throws JobExecutionException
    {
        long startTime = System.currentTimeMillis();
        logger.debug("Starting job . . .");
        logger.debug("Completed in : "
                + String.valueOf(System.currentTimeMillis() - startTime));
    }
}

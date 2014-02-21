package edu.yale.library.engine.cron;


import edu.yale.library.TimeUtils;
import edu.yale.library.beans.Monitor;
import edu.yale.library.beans.User;
import edu.yale.library.dao.UserDAO;
import edu.yale.library.dao.hibernate.UserHibernateDAO;
import edu.yale.library.engine.exports.DefaultExportEngine;
import edu.yale.library.engine.exports.ExportCompleteEventBuilder;
import edu.yale.library.engine.exports.ExportEngine;
import edu.yale.library.engine.model.ImportEngineException;
import edu.yale.library.events.Event;
import edu.yale.library.events.NotificationEventQueue;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportJob implements Job
{
    private final Logger logger = getLogger(this.getClass());

    /**
     * Export Job reads content rows from import job tables and writes to a spreadsheet
     * todo find out how to tie user id to file data
     *
     * @param ctx
     * @throws org.quartz.JobExecutionException
     *
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException
    {
        logger.debug("[start] export job.");

        ExportEngine exportEngine = new DefaultExportEngine();

        try
        {
            final long startTime = System.currentTimeMillis();
            final List list = exportEngine.read();

            logger.debug("Read rows from import tables. list size=" + list.size());

            final Monitor monitorItem = (Monitor) ctx.getJobDetail().getJobDataMap().get("event");

            assert(monitorItem != null);

            exportEngine.write(list, tmpFile(monitorItem.getExportPath()));

            logger.debug("Finished writing content rows to spreadsheet.");
            logger.debug("[end] Completed export job in " + TimeUtils.elapsedMilli(startTime));

            /* Add params as desired */
            final Event exportEvent = new ExportCompleteEventBuilder().setRowsProcessed(list.size()).createExportCompleteEvent();

            logger.debug("Adding export event; notifying user registered for this event instance. .");

            sendNotification(exportEvent, Collections.singletonList(monitorItem.getUser())); //todo

            logger.debug("Added export event to notification queue.");
        }
        catch (IOException e)
        {
            logger.error("Error executing job", e.getMessage());
            throw new ImportEngineException(e);
        }
    }

    private void sendNotification(final Event exportEvent, final List<User> u)
    {
        NotificationEventQueue.addEvent(new NotificationEventQueue().new NotificationItem(exportEvent, u));
    }

    private String tmpFile(final String folder)
    {
        logger.debug("Generate tmp name");
        return folder + System.getProperty("file.separator") + "export-results.xlsx"; //todo
    }


}

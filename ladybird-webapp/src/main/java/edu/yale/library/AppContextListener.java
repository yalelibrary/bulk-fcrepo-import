package edu.yale.library;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.yale.library.persistence.HibernateUtil;

import java.util.concurrent.TimeUnit;

public class AppContextListener implements ServletContextListener
{
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);
    private static long start = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        logger.debug("Application Start up.");
        try
        {
            start = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");
        } catch (Throwable t)
        {
            logger.error("Error in context initialization", t);
            t.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        try
        {
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time : " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + "minutes.");

        } catch (Throwable t)
        {
            logger.error("Error in context shutdown", t);
            t.printStackTrace();
        }
    }
}

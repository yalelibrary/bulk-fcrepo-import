package edu.yale.library;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.yale.library.persistence.HibernateUtil;

public class AppContextListener implements ServletContextListener
{
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);
    private static long START_HIBERNATE = 0;
    private static long START_DB = 0;

    private ServicesManager servicesManager;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        logger.debug("Application Start up.");
        try
        {
            //TODO read from props if the setting is embedded
            servicesManager.initDB();
            START_DB = System.currentTimeMillis();
            logger.debug("Started embedded DB");
            START_HIBERNATE = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
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
            //TODO read from props if the setting is embedded and a DB is indeed running
            servicesManager.stopDB();
            logger.debug("Closed embedded database. Time : " + TimeUtils.elapsedMinutes(START_DB));
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time : " + TimeUtils.elapsedMinutes(START_HIBERNATE));
        } catch (Throwable t)
        {
            logger.error("Error in context shutdown", t);
            t.printStackTrace();
        }
    }

    public AppContextListener()
    {
        servicesManager = new ServicesManager(); //
    }
}

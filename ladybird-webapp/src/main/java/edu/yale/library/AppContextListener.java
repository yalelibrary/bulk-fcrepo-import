package edu.yale.library;

import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.kernel.TimeUtils;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.JobModule;
import edu.yale.library.ladybird.kernel.KernelContext;
import edu.yale.library.ladybird.persistence.HibernateUtil;
import edu.yale.library.ladybird.persistence.ServicesManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);
    private static long START_HIBERNATE = 0;
    private static long START_DB = 0;

    private ServicesManager servicesManager;

    /* Contains test fdids corresponding to test excel file (instead of via db) */
    private static final String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.debug("Application Start up.");
        try {
            if (System.getProperty("file.encoding").equals("UTF-8")) {
                logger.warn("UTF-8 file encoding not detected.");
            }

            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to start embedded DB");
                servicesManager.initDB();
                START_DB = System.currentTimeMillis();
                logger.debug("Started embedded DB");
            }
            START_HIBERNATE = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");

            //bootstrap notification:
            KernelContext kernelContext = new KernelContext();
            kernelContext.setAbstractModule(new JobModule());
            KernelContext.initNotificationScheduler();

            setTextFieldDefsMap();
        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
            t.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            //TODO check state to ensure DB is running
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to stop embedded DB");
                servicesManager.stopDB();
                logger.debug("Closed embedded database. Time : " + TimeUtils.elapsedMinutes(START_DB));
            }
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time : " + TimeUtils.elapsedMinutes(START_HIBERNATE));
        } catch (Throwable t) {
            logger.error("Error in context shutdown", t);
            t.printStackTrace();
        }
    }

    /**
     * Helps in initing fdids via a text file
     *
     * @return
     * @throws java.io.IOException
     * @throws NullPointerException
     */
    @Deprecated
    public void setTextFieldDefsMap() throws IOException {
        Map<String, FieldConstant> fdidsMap = new HashMap<>();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            fdidsMap.put(properties.getProperty(fdidInt), getFdid(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt)));
        }
        FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        fieldDefinitionValue.setFieldDefMap(fdidsMap);
    }

    //TODO remove
    public FieldDefinitionValue getFdid(int fdid, String s) {
        return new FieldDefinitionValue(fdid, s);
    }


    public AppContextListener() {
        servicesManager = new ServicesManager(); //
    }
}

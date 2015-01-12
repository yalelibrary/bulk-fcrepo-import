package edu.yale.library.ladybird.persistence;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public final class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    /**
     * Builds org.hibernate.SessionFactory using a configuration file.
     *
     * @return org.hibernate.SessionFactory
     */
    private static SessionFactory buildSessionFactory() throws Exception {
        try {
            Configuration configuration = new Configuration();
            final String cfgXmlFile = new ConfigReader().getConfigFile();
            logger.debug("Using config file={}", cfgXmlFile);
            configuration.configure(cfgXmlFile);
            ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder().
                    applySettings(configuration.getProperties());
            logger.debug("Building session factory");
           SessionFactory sessionFactory = configuration.
                    buildSessionFactory(serviceRegistryBuilder.buildServiceRegistry());
            return sessionFactory;
        } catch (Exception ex) {
            logger.error("Exception encountered while building session factory");
            throw new Exception(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    try {
                        sessionFactory = buildSessionFactory();
                    } catch (Exception e) {
                        logger.error("Error getting session factory", e);
                    }
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        logger.debug("Shutting down session factory");
        try {
            getSessionFactory().close();
        } catch (HibernateException e) {
            throw new HibernateException(e);
        }
    }

    private static class ConfigReader {
        public String getConfigFile() throws Exception {
            ConfigReader configReader = new ConfigReader();
            return configReader.doGetConfigFileName();
        }

        /**
         * Sets the config file name.
         * TODO cleanup
         *
         * @return Name of configuration file
         * @throws Exception
         */
        private String doGetConfigFileName() throws Exception {
            try {
                if (getProperty(ApplicationProperties.DATABASE_STRING_IDENTIFIER).
                        equalsIgnoreCase(ApplicationProperties.DATABASE_DEFAULT_IDENTIFIER)) {
                    return ApplicationProperties.DEFAULT_HIBERNATE_FILE;
                } else {
                    return ApplicationProperties.CUSTOM_HIBERNATE_FILE;
                }
            } catch (Exception e) {
                throw new Exception("DB config invalid or incomplete. ", e);
            }
        }

        /**
         * Reads a property
         *
         * @param p Property identifier
         * @return Property value
         * @throws Exception
         */
        private String getProperty(final String p) throws Exception {
            return readPropertiesFromFile(p, "/" + ApplicationProperties.PROPS_FILE);
        }

        private String readPropertiesFromFile(final String p, final String path) throws Exception {
            try {
                Properties properties = new Properties();
                properties.load(this.getClass().getResourceAsStream(path));
                return properties.getProperty(p);
            } catch (IOException | NullPointerException e) {
                logger.error("Error reading property={}", e.getMessage());
                logger.error("The path or file to be read was={}", path);
                logger.error("The property to be read was={}", p);
                throw new Exception(e);
            }
        }

    }

}
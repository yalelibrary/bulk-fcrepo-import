package edu.yale.library;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties {

    public static final String PROPS_FILE = "ladybird.properties";
    public static final String DATABASE_STRING_IDENTIFIER = "database";
    public static final String DATABASE_DEFAULT_IDENTIFIER = "default";
    public static final String DEFAULT_HIBERNATE_FILE = "default.hibernate.cfg.xml";
    public static final String CUSTOM_HIBERNATE_FILE = "hibernate.cfg.xml";
    public static final boolean RUN_WITH_INCOMPLETE_CONFIG = false;
    public static final String ALREADY_RUNNING = "Driver already RUNNING.";
    public static final String ALREADY_STOPPED = "Driver already STOPPED.";
    public static final String SCHEMA_PROPS_FILE = "/default.schema.properties";

    public static boolean runWithIncompleteDBConfig() {
        return ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG;
    }

    /**
     *
     */
    static final class CONFIG_STATE {

        private static final Logger logger = LoggerFactory.getLogger(CONFIG_STATE.class);

        private static PropertiesConfiguration cfg;

        static {
            try {
                cfg = new PropertiesConfiguration(PROPS_FILE);
            } catch (ConfigurationException t) {
                logger.error("Error setting up configuration file");
            }
        }

        public static final boolean DEFAULT_DB_CONFIGURED = getConfig();

        /**
         * Ignores exception if prop not set and just returns false.
         *
         * @return
         */
        private static boolean getConfig() {
            try {
                return readProperty(DATABASE_STRING_IDENTIFIER).equals(DATABASE_DEFAULT_IDENTIFIER);
            } catch (Exception e) {
                return false;
            }
        }

        private static String readProperty(String s) {
            return cfg.getString(s);
        }

    }
}

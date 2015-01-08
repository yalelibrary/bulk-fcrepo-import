package edu.yale.library.ladybird.persistence;

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
    public static final String ALREADY_RUNNING = "Driver already RUNNING.";
    public static final String ALREADY_STOPPED = "Driver already STOPPED.";
    public static final String SCHEMA_PROPS_FILE = "/derby.schema.properties";
    public static final String KILL_SCHEMA_PROPS_FILE = "/derby.kill.schema.properties";

    public static final class CONFIG_STATE {

        private static final Logger logger = LoggerFactory.getLogger(CONFIG_STATE.class);

        private static PropertiesConfiguration cfg;

        static {
            try {
                cfg = new PropertiesConfiguration(PROPS_FILE);
            } catch (ConfigurationException t) {
                logger.error("Error setting up configuration file");
            }
        }

    }
}

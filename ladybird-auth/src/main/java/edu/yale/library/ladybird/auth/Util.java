package edu.yale.library.ladybird.auth;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    private static Configuration config;

    static {
        try {
           config = new PropertiesConfiguration("ladybird.properties");
        } catch (Exception e) {
            logger.error("Error setting property file", e);
        }
    }

    public static String getProperty(final String p) {
        return config.getProperty(p).toString();
    }
}

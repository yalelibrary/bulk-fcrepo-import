package edu.yale.library.ladybird.auth;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General utility class. Subject to removal
 *  @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class PropertiesConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigUtil.class);

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

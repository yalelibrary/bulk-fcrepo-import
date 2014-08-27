package edu.yale.library.ladybird.kernel;

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
    public static final String SCHEMA_PROPS_FILE = "/derby.schema.properties";
    public static final String FDID_PROPS_FILE = "/fdid.schema.properties";
    public static final String KILL_SCHEMA_PROPS_FILE = "/derby.kill.schema.properties";
    private static final String EMAIL_PORT_IDENTIFIER = "mail_port";
    private static final String HOST_NAME_IDENTIFIER = "mail_host";
    private static final String ADMIN_EMAIL_IDENTIFIER = "mail_admin";
    public static final String IMAGE_MAGICK_PATH_ID = "image_magick_path";
    public static final String IMPORT_ROOT_PATH_ID = "import_root_path";
    public static final String NO_IMAGE_FOUND_PATH = "no_image_found_path";

    public static boolean runWithIncompleteDBConfig() {
        return ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG;
    }

    /**
     *
     */
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

        public static final boolean DEFAULT_DB_CONFIGURED = getConfig();
        public static final String EMAIL_ADMIN = getAdminEmail();
        public static final int EMAIL_PORT = getEmailPort();
        public static final String EMAIL_HOST = getEmailHost();
        public static final String IMAGE_MAGICK_PATH = getImageMagickCommandPath();
        public static final String IMPORT_ROOT_PATH = getImportRootPath();
        public static final String NO_IMAGE_FOUND_FILE = getNoImageFoundFilePath();

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

        private static String getAdminEmail() {
            return readProperty(ADMIN_EMAIL_IDENTIFIER);
        }

        private static int getEmailPort() {
            return readIntProperty(EMAIL_PORT_IDENTIFIER);
        }

        private static String getEmailHost() {
            return readProperty(HOST_NAME_IDENTIFIER);
        }

        private static String getImageMagickCommandPath() {
            return readProperty(IMAGE_MAGICK_PATH_ID);
        }

        private static String getImportRootPath() {
            return readProperty(IMPORT_ROOT_PATH_ID);
        }

        private static String readProperty(String s) {
            return cfg.getString(s);
        }

        private static int readIntProperty(String s) {
           return cfg.getInt(s);
        }

        private static String getNoImageFoundFilePath() {
            return readProperty(NO_IMAGE_FOUND_PATH);
        }

    }
}

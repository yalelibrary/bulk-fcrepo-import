package edu.yale.library.ladybird.kernel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ApplicationProperties {

    public static final String DEFAULT_PROPS_FILE = "ladybird.properties";

    public static final String DATABASE_STRING_IDENTIFIER = "database";

    public static final String DATABASE_DEFAULT_IDENTIFIER = "default";

    public static final String SCHEMA_PROPS_FILE = "/derby.schema.properties";

    public static final String FDID_PROPS_FILE = "/fdid.schema.properties";

    public static final String KILL_SCHEMA_PROPS_FILE = "/derby.kill.schema.properties";

    public static final String IMAGE_MAGICK_PATH_ID = "image_magick_path";

    public static final String IMPORT_ROOT_PATH_ID = "import_root_path";

    public static final String NO_IMAGE_FOUND_PATH = "no_image_found_path";

    public static final class CONFIG_STATE {

        private static final Logger logger = LoggerFactory.getLogger(CONFIG_STATE.class);

        private static PropertiesConfiguration cfg;

        static {
            try {
                final String altPath = System.getenv("ladybird_props");

                if (altPath != null && Files.exists(Paths.get(altPath))) {
                    logger.info("Reading config props from file={}",  altPath);
                    cfg = new PropertiesConfiguration(altPath);
                } else {
                    logger.info("Reading config props from default file={}", DEFAULT_PROPS_FILE);
                    cfg = new PropertiesConfiguration(DEFAULT_PROPS_FILE);
                }
            } catch (ConfigurationException t) {
                logger.error("Error setting up configuration file");
            }
        }

        public static final boolean DEFAULT_DB_CONFIGURED = isDefaultDbConfig();

        public static final String EMAIL_ADMIN = getAdminEmail();

        public static final int EMAIL_PORT = getEmailPort();

        public static final String EMAIL_HOST = getEmailHost();

        public static final String IMAGE_MAGICK_PATH = getImageMagickCommandPath();

        public static final String IMPORT_ROOT_PATH = getImportRootPath();

        public static final String NO_IMAGE_FOUND_FILE = getNoImageFoundFilePath();

        public static final String WELCOME_PAGE = getWelcomePage();

        /**
         * Ignores exception if prop not set and just returns false.
         */
        private static boolean isDefaultDbConfig() {
            try {
                return readProperty(DATABASE_STRING_IDENTIFIER).equals(DATABASE_DEFAULT_IDENTIFIER);
            } catch (Exception e) {
                return false;
            }
        }

        private static String getAdminEmail() {
            return readProperty("mail_admin");
        }

        private static int getEmailPort() {
            return readIntProperty("mail_port");
        }

        private static String getEmailHost() {
            return readProperty("mail_host");
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

        private static String getWelcomePage() {
            return readProperty("welcome_page");
        }

    }
}

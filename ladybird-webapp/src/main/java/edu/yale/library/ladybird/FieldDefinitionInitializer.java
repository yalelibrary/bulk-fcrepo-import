package edu.yale.library.ladybird;

import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Utility for field definition(fdid) initialization.
 */
public class FieldDefinitionInitializer {

    private final Logger logger = LoggerFactory.getLogger(FieldDefinitionInitializer.class);

    /** Contains fdids meant to be loaded into db */
    private static final String FDID_INITIAL_PROPS_FILE = "/fdids.initial.properties";

    /**
     * Initializes fdids.
     */
    public void setInitialFieldDefinitionDb() throws IOException {
        logger.debug("Initializing fdids from file={}", FDID_INITIAL_PROPS_FILE);

        FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_INITIAL_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            FieldDefinition f = getFdid(Integer.parseInt(fdidInt), properties.getProperty(fdidInt));
            try {
                fieldDefinitionDAO.save(f);
            } catch (Exception e) {
                logger.error("Error saving fdid", e);  //ignore
            }
        }
        logger.debug("fdid size={}", fieldDefinitionDAO.count());
    }

    private FieldDefinition getFdid(final int fdid, final String s) {
        return new FieldDefinition(fdid, s, new Date());
    }
}

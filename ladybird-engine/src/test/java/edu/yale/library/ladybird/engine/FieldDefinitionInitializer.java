package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Helps in fdid initialization. Subject to removal.
 */
public class FieldDefinitionInitializer {

    private final Logger logger = LoggerFactory.getLogger(FieldDefinitionInitializer.class);

    /** Contains fdids meant to be loaded into db for init */
    private static final String FDID_INITIAL_PROPS_FILE = "/fdids.test.properties";

    /**
     * Initializes fdids. This concept needs to be re-visited.
     */
    public void setInitialFieldDefinitionDb() throws IOException {
        logger.debug("Init db with fdid from file={}", FDID_INITIAL_PROPS_FILE);

        FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_INITIAL_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            FieldDefinition f = getFdid(Integer.parseInt(fdidInt), properties.getProperty(fdidInt));
            try {
                fieldDefinitionDAO.save(f);
            } catch (Exception e) {
                logger.debug("Error saving fdid", e);  //ignore
            }
        }
    }

    private FieldDefinition getFdid(final int fdid, final String s) {
        FieldDefinition fieldDefinition = new  FieldDefinition(fdid, s);
        //fieldDefinition.setLocked(false);
        return fieldDefinition;
    }
}

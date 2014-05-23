package edu.yale.library.ladybird;

import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Helps in fdid initialization. Subject to removal.
 */
public class FieldDefinitionInitializer {

    private final Logger logger = LoggerFactory.getLogger(FieldDefinitionInitializer.class);

    /** Contains test fdids corresponding to test excel file (instead of via db) */
    private static final String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    /** Contains fdids meant to be loaded into db for init */
    private static final String FDID_INITIAL_PROPS_FILE = "/fdids.initial.properties";

    /**
     * Helps in initing fdids via a text file
     * @throws java.io.IOException
     */
    public void setTextFieldDefsMap() throws IOException {
        logger.debug("Init map with fdid from file={} . . .", FDID_TEST_PROPS_FILE);

        Map<String, FieldConstant> fdidsMap = new HashMap<>();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            fdidsMap.put(properties.getProperty(fdidInt), getFdidValue(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt)));
        }
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        fieldDefinitionValue.setFieldDefMap(fdidsMap);
    }

    /**
     * Initializes fdids. This concept needs to be re-visited.
     */
    public void setInitialFieldDefinitionDb() throws IOException {
        logger.debug("Init db with fdid from file={} . . .", FDID_INITIAL_PROPS_FILE);

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

    private FieldDefinitionValue getFdidValue(final int fdid, final String s) {
        return new FieldDefinitionValue(fdid, s);
    }

    private FieldDefinition getFdid(final int fdid, final String s) {
        return new FieldDefinition(fdid, s);
    }
}

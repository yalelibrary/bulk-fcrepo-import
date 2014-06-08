package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FieldConstantRules {

    private static final Logger logger = LoggerFactory.getLogger(FieldConstantRules.class);

    /**
     * Determines if a column is an OAI field.
     * @param col A spreadsheet column value
     * @return whether a column matches an official OAI field
     */

    public static boolean isOAIFunction(final ImportEntity.Column col) {
        final String fieldName = col.getField().getName();
        return (fieldName.equals(FunctionConstants.F104.getName()))
                || fieldName.equals(FunctionConstants.F105.getName()) ? true : false;
    }

    /**
     * Determines if a column is a function that should kick off some sort of image processing.
     * @param col A spreadsheet column value
     * @return whether a column matches an image processing function
     */

    public static boolean isImageProcessingFunction(final ImportEntity.Column col) {
        final String fieldName = col.getField().getName();
        return (fieldName.equals(FunctionConstants.F3.getName())) ? true : false;
    }

    public static boolean isF1Function(final ImportEntity.Column col) {
        final String fieldName = col.getField().getName();
        return (fieldName.equals(FunctionConstants.F1.getName())) ? true : false;
    }

    /**
     * Gets all FieldConstants (function constants, fdids etc) pertaining to the application
     * @return list of field constants
     */
    public static List<FieldConstant> getApplicationFieldConstants() {

        final List<FieldConstant> globalFunctionConstants = new ArrayList<>();

        //add fdids via db
        FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();
        List<FieldDefinition> fieldDefinitions = fieldDefinitionDAO.findAll();

        globalFunctionConstants.addAll(fieldDefinitions);

        //add F1, F104 etc
        final FunctionConstants[] functionConstants = FunctionConstants.values();

        for (final FieldConstant f: functionConstants) {
            globalFunctionConstants.add(f);
        }
        return globalFunctionConstants;
    }

    /**
     * Converts string to a FieldConstant (fdid or a FunctionConstants)
     * @param value
     * @return
     */
    public static FieldConstant convertStringToFieldConstant(final String value) {

        try {
            int fdidInt = fdidAsInt(value);

            FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();
            FieldDefinition fieldDefinition = fieldDefinitionDAO.findByFdid(fdidInt);

            if (fieldDefinition != null) {
                return fieldDefinition;

            }
        } catch (Exception e) {
            logger.error("Could not convert, seeing if it's a function constant");
        }

        //Otherwise, see if it's a function constant
        try {
            final FunctionConstants f = FunctionConstants.valueOf(value);
            return f;
        } catch (Exception e) {
            logger.debug("Error converting to FieldConstant(FunctionConstant) value={}", value);
        }

        return null;
    }

    /**
     * converter helper
     * TODO this depends on the the fdids are loaded (currently through a file fdid.test.propeties at webapp start up)
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     *
     * @see edu.yale.library.ladybird.engine.model.FieldConstantRules#convertStringToFieldConstant(String)
     * for similiar functionality
     */
    private static Integer fdidAsInt(String s) {
        logger.debug("Converting={}", s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String[] parsedString = s.split("fdid=");
            return Integer.parseInt(parsedString[1].replace("}", ""));
        }
    }

    /**
     * Note: Mappings are defined via db, and will be injected.
     * @param fieldConstant
     * @return
     */
    @Deprecated
    public static Marc21Field getFieldConstantToMarc21Mapping(final FieldConstant fieldConstant) {
        logger.error("Mappings are supposed to be injected via db");
        String f = fieldConstant.getName();
        if (f.equals("70") || f.equals("Title{fdid=70}") || f.equals("fdid=70")) {
            logger.debug("Returning for this field, Marc tag={}", Marc21Field._245.toString());
            return Marc21Field._245;
        }
        logger.debug("Returning for this field, Marc tag={}", Marc21Field.UNK);
        return Marc21Field.UNK;
    }
}

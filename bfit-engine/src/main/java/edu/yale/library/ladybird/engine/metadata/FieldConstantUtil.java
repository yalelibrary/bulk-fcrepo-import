package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.UnknownFieldConstantException;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class FieldConstantUtil {

    private static final Logger logger = LoggerFactory.getLogger(FieldConstantUtil.class);

    /**
     * Determines if a column is an OAI field.
     *
     * @param col A spreadsheet column value
     * @return whether a column matches an official OAI field
     */
    public static boolean isOAIFunction(final Import.Column col) {
        final String field = col.getField().getName();
        return (field.equals(FunctionConstants.F104.getName())) || field.equals(FunctionConstants.F105.getName());
    }

    /**
     * Gets all FieldConstants (function constants, fdids etc) pertaining to the application
     *
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

        for (final FieldConstant f : functionConstants) {
            globalFunctionConstants.add(f);
        }
        return globalFunctionConstants;
    }

    /**
     * Shouldn't be called again and again due to DB ops
     *
     * Converts string to a FieldConstant (fdid or a FunctionConstants)
     *
     * @param value
     * @return
     */
    public static FieldConstant toFieldConstant(final String value) {

        try {
            return FunctionConstants.valueOf(value.toUpperCase());
        } catch (Exception e) {
            //logger.trace("Not a FunctionConstant value={}", value);
        }

        //Otherwise see if it's an fdid
        try {
            int fdidInt = FieldDefinition.fdidAsInt(value);

            FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();
            FieldDefinition fieldDefinition = fieldDefinitionDAO.findByFdid(fdidInt);

            if (fieldDefinition != null) {
                return fieldDefinition;
            }
        } catch (Exception e) {
            //logger.trace("Could not convert");
        }

        logger.trace("Couldn't convert param={} to a FieldConstant", value);
        return null;
    }

    /**
     * Transform a strihng into a a FieldConstant
     *
     * @param value spreadsheet cell value
     * @return a FieldConstant
     * @throws edu.yale.library.ladybird.engine.model.UnknownFieldConstantException
     *
     */
    public static FieldConstant getFieldConstant(final String value) throws UnknownFieldConstantException {
        final FieldConstant f = FieldConstantUtil.toFieldConstant(value);

        if (f != null) {
            return f;
        }

        //Try converting it to function constant (redundantly):
        try {
            final String normCellString = value.replace("{", "").replace("}", "");
            return FunctionConstants.valueOf(normCellString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownFieldConstantException(value + " unknown function or fdid.");
        }
    }


    /**
     * Helps determine fdid to table mapping
     * @param f int fdid
     * @return whether a value is a string
     */
    public static boolean isString(int f) {
        return new FieldDefinitionHibernateDAO().findByFdid(f).getAcid() == 0;
    }

    //FIXME
    public static boolean isDropDown(final FieldDefinition fieldDefinition) {
        return fieldDefinition.getType().equalsIgnoreCase("dropdown");

    }



}

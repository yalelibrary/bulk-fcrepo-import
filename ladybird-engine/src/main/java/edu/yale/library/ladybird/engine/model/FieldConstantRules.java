package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
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
     * Gets all FieldConstants (fdids etc) pertaining to the application
     * @return list of field constants
     * TODO move
     */
    public static List<FieldConstant> getApplicationFieldConstants() {

        final List<FieldConstant> globalFunctionConstants = new ArrayList<>();

        //add fdids
        final Map<String, FieldConstant> fdidMap = FieldDefinitionValue.getFieldDefMap();
        final Set<String> keySet = fdidMap.keySet();

        for (final String s: keySet) {
            final FieldConstant f = fdidMap.get(s);
            globalFunctionConstants.add(f);
        }

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
     * //TODO move
     */
    public static FieldConstant convertStringToFieldConstant(final String value) {
        final Map<String, FieldConstant> map =  FieldDefinitionValue.getFieldDefMap();
        //logger.debug(map.toString());
        final FieldConstant val;
        try {
            val = map.get(value);
            //logger.debug("Found val={}", val.toString());
            return val;
        } catch (Exception e) {
            logger.warn("Error converting to FieldConstant(FieldDefinition) value={}", value);
        }

        //See if it's a function constant
        try {
            final FunctionConstants f  = FunctionConstants.valueOf(value);
            return f;
        } catch (Exception e) {
            logger.warn("Error converting to FieldConstant(FunctionConstant) value={}", value);
        }

        return null;
    }

    /**
     * Note: Mappings are defined via db, and will be injected.
     * @param fieldConstant
     * @return
     */
    @Deprecated
    public static Marc21Field getFieldConstantToMarc21Mapping(final FieldConstant fieldConstant) {
        if (fieldConstant.getName().equals("70") || fieldConstant.getName().equals("Title{fdid=70}")) {
            return Marc21Field._245;
        }
        return Marc21Field.UNK;
    }


}

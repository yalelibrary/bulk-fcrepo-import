package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntity;

/**
 *
 */
public class FunctionConstantsRules {

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
}

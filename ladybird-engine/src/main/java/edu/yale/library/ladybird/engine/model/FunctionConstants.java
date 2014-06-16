package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.entity.FieldConstant;

/**
 * LB processing function constants.
 *
 */
public enum FunctionConstants implements FieldConstant {
    /**
     * Unspecified function.
     */
    UNK("UNK", "UNKNOWN-FUNCTION"),
    /**
     * Oid *
     */
    F1("F1", "Oid"),
    /**
     * File location *
     */
    F3("F3", "Image"),
    /**
     * F4
     */
    F4("F4", "complex-object-1"),
    /**
     * F4
     */
    F5("F5", "complex-object-2"),
    /**
     * F4
     */
    F6("F6", "complex-object-3"),
    /**
     * F4
     */
    F7("F7", "complex-object-4"),
    /**
     * F4
     */
    F8("F8", "complex-object-5"),
    /**
     * File attached to an OID from the import folder
     */
    F31("F31", "OID-ATTACHED-IN-IMPORT-FOLDER"),
    /**
     * File which represents an OID from the /OID/ folder, outside /import
     */
    F32("F31", "OID-ATTACHED-IN-OTHER-FOLDER"),
    /**
     * File representing an OID from the /tracking folder, outside /import
     */
    F33("F31", "OID-ATTACHED-IN-TRACKING-FOLDER"),
    /**
     * HTTP link to an external file
     */
    F300("F300", "EXTERNAL-FILE-HTTP-LINK"),
    /**
     * An external file that must be pulled into the local file store for processing
     */
    F301("F301", "EXTERNAL-FILE-PULL"),
    /**
     * BibId Import
     */
    F104("F104", "OAI-BibId-Import"),
    /**
     * Barcode Import
     */
    F105("F105", "OAI-Barcode-Import"),
    /**
     * places objects into hydra table for staging into hydra_publish
     */
    F40("F40", "Hydra-Publish-Staging"),
    /**
     * oid pointer
     */
    F11("F11", "oid-pointer"),
    /**
     * Deletes
     */
    F00("F00", "oid-delete");

    private String name;
    private String title;

    FunctionConstants(String s, String t) {
        this.name = s;
        this.title = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static boolean isFunction(String s) {
        try {
            FunctionConstants.valueOf(s.toUpperCase());
        } catch (Exception e) {
           return false;
        }
        return true;
    }
}

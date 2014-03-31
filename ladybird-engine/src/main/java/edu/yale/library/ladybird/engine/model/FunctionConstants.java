package edu.yale.library.ladybird.engine.model;

/**
 * Business logic level (or so it seems) column header constants.
 *
 * @see FieldDefinitionValue
 *      <p/>
 *      TODO revise name,title per logic requirements
 */
public enum FunctionConstants implements FieldConstant {
    /**
     * unspecified function. @see ReadMode
     */
    UNK("Test", "Test"),
    /**
     * Oid *
     */
    F1("F1", "Oid"),
    /**
     * File location *
     */
    F3("F3", "Image"),

    /**
     * BibId Import
     */
    F104("F104", "OAI-BibId-Import"),
    /**
     * Barcode Import
     */
    F105("F105", "OAI-Barcode-Import");

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
}

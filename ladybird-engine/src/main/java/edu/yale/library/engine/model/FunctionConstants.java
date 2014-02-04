package edu.yale.library.engine.model;

/**
 * Business logic level (or so it seems) column header constants.
 *
 * @see FieldDefinitionValue
 *
 * TODO revise name,title per logic requirements
 *
 */
public enum FunctionConstants implements FieldConstant
{
    /** unspecified function. @see ReadMode */
    UNK("Test", "Test"),
    /** Oid **/
    F1("F1", "Oid"),
    /** File location **/
    F3("F3", "Image");

    private String name;
    private String title;

    FunctionConstants(String s, String t)
    {
        this.name = s;
        this.title = t;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String s)
    {
        this.name = s;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}

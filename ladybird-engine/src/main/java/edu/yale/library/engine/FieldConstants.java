package edu.yale.library.engine;

/**
 * Override as required for logic. Subject to modification per logic requirement.
 */
public enum FieldConstants
{
    UNK("Test", "Test"),
    F1("?", "?"),
    FDID90("Subject, topic", "Subject, topic{fdid=90}");

    private String name;   //?
    private String title;  //?

    FieldConstants(String s, String t)
    {
        this.name = s;
        this.title = t;
    }

    public String getName()
    {
        return name;
    }

    void setName(String s)
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

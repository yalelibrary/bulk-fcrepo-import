package edu.yale.library;

public class ApplicationProperties
{
    public static final String PROPS_FILE = "ladybird.properties";
    public static final String DATABASE_STRING_IDENTIFIER = "database";
    public static final String DATABASE_DEFAULT_IDENTIFIER = "default";
    public static final String DEFAULT_HIBERNATE_FILE = "default.hibernate.cfg.xml";
    public static final String CUSTOM_HIBERNATE_FILE = "hibernate.cfg.xml";
    public static boolean RUN_WITH_INCOMPLETE_CONFIG = false;

    public static boolean runWithIncompleteDBConfig()
    {
        return ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG;
    }
}

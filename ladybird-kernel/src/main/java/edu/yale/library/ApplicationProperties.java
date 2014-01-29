package edu.yale.library;

public class ApplicationProperties
{
    public static final String PROPS_FILE = "ladybird.properties";
    public static final String DATABASE_STRING_IDENTIFIER = "database";
    public static final String DATABASE_DEFAULT_IDENTIFIER = "default";
    public static final String DEFAULT_HIBERNATE_FILE = "default.hibernate.cfg.xml";
    public static final String CUSTOM_HIBERNATE_FILE = "hibernate.cfg.xml";
    public static boolean RUN_WITH_INCOMPLETE_CONFIG = false;
    public static final String ALREADY_RUNNING = "Driver already RUNNING.";
    public static final String ALREADY_STOPPED = "Driver already STOPPED.";
    public static final String SCHEMA_PROPS_FILE = "/default.schema.properties";


    public static boolean runWithIncompleteDBConfig()
    {
        return ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG;
    }
}

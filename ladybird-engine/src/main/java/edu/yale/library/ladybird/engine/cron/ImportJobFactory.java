package edu.yale.library.ladybird.engine.cron;

/**
 *
 */
public class ImportJobFactory {

    private static ImportJob instance;

    public static ImportJob getInstance() {
        if (instance == null) {
            return new DefaultImportJob();
        }
        return instance;
    }

    public static void setInstance(ImportJob importJob) {
        instance = importJob;
    }
}

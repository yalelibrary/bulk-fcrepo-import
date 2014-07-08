package edu.yale.library.ladybird.engine.cron;

public class ExportMailerFactory {

    private static ExportMailerJob instance;

    public static ExportMailerJob getInstance() {
        if (instance == null) {
            return new DefaultExportMailerJob();
        }
        return instance;
    }

    public static void setInstance(ExportMailerJob exportMailerJob) {
        instance = exportMailerJob;
    }
}

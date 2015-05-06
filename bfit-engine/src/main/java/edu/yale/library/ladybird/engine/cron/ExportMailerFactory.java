package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.cron.job.DefaultExportMailerJob;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
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

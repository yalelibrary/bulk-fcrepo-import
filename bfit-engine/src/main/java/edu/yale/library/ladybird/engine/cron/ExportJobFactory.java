package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.cron.job.DefaultExportJob;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ExportJobFactory {

    private static ExportJob instance;

    public static ExportJob getInstance() {
        if (instance == null) {
            return new DefaultExportJob();
        }
        return instance;
    }

    public static void setInstance(ExportJob importJob) {
        instance = importJob;
    }
}

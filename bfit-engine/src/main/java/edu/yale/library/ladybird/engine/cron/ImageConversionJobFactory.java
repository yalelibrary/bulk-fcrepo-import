package edu.yale.library.ladybird.engine.cron;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImageConversionJobFactory {

    private static ImageConversionJob instance;

    public static ImageConversionJob getInstance() {
        if (instance == null) {
            return new ImageConversionJob();
        }
        return instance;
    }

    public static void setInstance(ImageConversionJob exportMailerJob) {
        instance = exportMailerJob;
    }
}

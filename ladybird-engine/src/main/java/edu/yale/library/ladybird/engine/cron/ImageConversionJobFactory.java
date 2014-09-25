package edu.yale.library.ladybird.engine.cron;

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

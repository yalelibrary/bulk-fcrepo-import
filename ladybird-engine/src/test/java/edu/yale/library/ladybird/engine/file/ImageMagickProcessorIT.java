package edu.yale.library.ladybird.engine.file;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ImageMagickProcessorIT {
    Logger logger = LoggerFactory.getLogger(ImageMagickProcessorIT.class);

    @Test
    public void shouldResizeImage() {
        final ImageMagickProcessor imageMagickProcessor = new ImageMagickProcessor();
        try {
            final String inputFile = getPath("lux-et-veritas.jpg");
            final String outputFile = inputFile.replace(".jpg", "");
            imageMagickProcessor.resizeImage(inputFile, outputFile, 200, 200);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException f) {
            logger.error(f.getMessage());
            //TODO remove
        }
        logger.debug("Image resized");
    }

    @Test
    public void shouldConverToJp2() {
        final ImageMagickProcessor imageMagickProcessor = new ImageMagickProcessor();
        try {
            imageMagickProcessor.converToJp2(getPath("lux-et-veritas.jpg"));
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException f) {
            logger.error(f.getMessage());
            //TODO remove
        }
    }

    private static String getPath(final String fileName) {
        return System.getProperty("ladybird-tests/src/test/resources/") + fileName;
    }

}

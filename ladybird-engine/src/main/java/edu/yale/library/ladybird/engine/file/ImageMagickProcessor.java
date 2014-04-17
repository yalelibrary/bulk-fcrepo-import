package edu.yale.library.ladybird.engine.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Processes files using ImageMagick or markes files for ImageMagick processing
 */
public class ImageMagickProcessor implements ImageProcessor {

    private Logger logger = LoggerFactory.getLogger(ImageMagickProcessor.class);


    @Override
     public void resizeImage(final String fileName, final String outputFileName, final int width, final int height)
            throws ImageProcessingException {
        logger.debug("Resizing image file={}", fileName);
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * This will be replaced with a call to the actual service.
     * @param fileName
     * @throws IOException
     */
    @Override
    public void converToJp2(final String fileName) {
        logger.debug("Conveting to jp2={}", fileName);
        throw new UnsupportedOperationException("Not implemented");
    }

}

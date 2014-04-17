package edu.yale.library.ladybird.engine.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Uses native Java image io for some tasks
 */
public class ImageIOProcessor implements ImageProcessor {

    private Logger logger = LoggerFactory.getLogger(ImageIOProcessor.class);


    @Override
    public void resizeImage(final String fileName, final String outputFileName, final int width, final int height)
            throws ImageProcessingException {
        logger.debug("Resizing image file={}", fileName);
        doResize(fileName, outputFileName, width, height);
    }

    private void doResize(final String fileName, final String outputFileName,
                          final int width, final int height) throws RuntimeException{
        try {
            final BufferedImage originalImage = ImageIO.read(new File(fileName));
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            final BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
            ImageIO.write(resizeImageJpg, "jpg", new File(outputFileName));
        } catch (IOException e) {
            throw new ImageProcessingException(e);
        }
    }

    private BufferedImage resizeImage(final BufferedImage originalImage,
                                      final int type, final int width, final int height){
        final BufferedImage resizedImage = new BufferedImage(width, height, type);
        final Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @Override
    public void converToJp2(String fileName) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

package edu.yale.library.ladybird.engine.file;

import org.junit.Test;

/**
 * TODO
 */
public class ImageMagickProcessorTest {

    @Test
    public void shouldConvertImage() {
        ImageMagickProcessor imageMagickProcessor = new ImageMagickProcessor();
        imageMagickProcessor.toFormat(null, null);
    }

    @Test
    public void shouldContainPath() {
        ImageMagickProcessor imageMagickProcessor = new ImageMagickProcessor();
        imageMagickProcessor.pathContains("");
    }

    @Test
    public void shouldGetImageMagickPath() {
        ImageMagickProcessor.getImgMagickPath();
    }
}

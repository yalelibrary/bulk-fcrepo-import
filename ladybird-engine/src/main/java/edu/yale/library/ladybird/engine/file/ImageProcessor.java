package edu.yale.library.ladybird.engine.file;

/**
 *
 */
public interface ImageProcessor extends FileProcessor {

    public void resizeImage(String fileName, String outFileName, int width, int height);

    public void converToJp2(String fileName);
}

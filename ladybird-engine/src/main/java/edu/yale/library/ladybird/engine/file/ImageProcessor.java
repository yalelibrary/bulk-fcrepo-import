package edu.yale.library.ladybird.engine.file;

/**
 *
 */
public interface ImageProcessor extends FileProcessor {

    void resizeImage(String fileName, String outFileName, int width, int height);

    void converToJp2(String fileName);
}

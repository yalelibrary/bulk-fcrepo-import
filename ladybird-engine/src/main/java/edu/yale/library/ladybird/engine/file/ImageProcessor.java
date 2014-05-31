package edu.yale.library.ladybird.engine.file;

/**
 *
 */
public interface ImageProcessor extends FileProcessor {

    void toFormat(String src, String dest);
}

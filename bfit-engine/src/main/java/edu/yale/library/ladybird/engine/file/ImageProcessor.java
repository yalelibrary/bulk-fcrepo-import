package edu.yale.library.ladybird.engine.file;


/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ImageProcessor extends FileProcessor {

    void toFormat(String src, String dest);
}

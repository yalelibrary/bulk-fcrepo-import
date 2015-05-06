package edu.yale.library.ladybird.engine.imports;

import java.io.InputStream;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class SpreadsheetFileBuilder {

    private String fileName;

    private String path;

    private InputStream fileStream;

    public SpreadsheetFileBuilder filename(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SpreadsheetFileBuilder filepath(String path) {
        this.path = path;
        return this;
    }

    public SpreadsheetFileBuilder stream(InputStream fileStream) {
        this.fileStream = fileStream;
        return this;
    }

    public Spreadsheet create() {
        return new Spreadsheet(fileName, path, fileStream);
    }
}
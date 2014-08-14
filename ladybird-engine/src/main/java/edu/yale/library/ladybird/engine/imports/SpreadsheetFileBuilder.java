package edu.yale.library.ladybird.engine.imports;

import java.io.InputStream;

public class SpreadsheetFileBuilder {
    private String fileName;
    private String altName;
    private String path;
    private InputStream fileStream;

    public SpreadsheetFileBuilder filename(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SpreadsheetFileBuilder altname(String altName) {
        this.altName = altName;
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

    public SpreadsheetFile create() {
        return new SpreadsheetFile(fileName, altName, path, fileStream);
    }
}
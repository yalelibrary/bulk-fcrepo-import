package edu.yale.library.ladybird.engine.imports;

import java.io.InputStream;

public class SpreadsheetFileBuilder {
    private String fileName;
    private String altName;
    private String path;
    private InputStream fileStream;

    public SpreadsheetFileBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SpreadsheetFileBuilder setAltName(String altName) {
        this.altName = altName;
        return this;
    }

    public SpreadsheetFileBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public SpreadsheetFileBuilder setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
        return this;
    }

    public SpreadsheetFile createSpreadsheetFile() {
        return new SpreadsheetFile(fileName, altName, path, fileStream);
    }
}
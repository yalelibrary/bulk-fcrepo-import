package edu.yale.library.ladybird.engine.imports;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a spreadsheet file.
 */
public class SpreadsheetFile implements Cloneable {
    private String fileName;

    private String altName;

    private String path;

    private InputStream fileStream;

    public SpreadsheetFile(String fileName, String altName, String path, InputStream fileStream) {
        this.fileName = fileName;
        this.altName = altName;
        this.path = path;
        this.fileStream = fileStream;
    }

    public String getAltName() {
        return altName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Return sheet
     *
     * @return XSSF sheet
     * @throws java.io.IOException
     */
    public XSSFSheet getDefaultSheet(int sheetNumber) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook(fileStream);
        final XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
        return sheet;
    }

    @Override
    public String toString() {
        return "SpreadsheetFile{"
                + "fileName='" + fileName + '\''
                + ", altName='" + altName + '\''
                + ", path='" + path + '\''
                + '}';
    }

    @Override
    protected SpreadsheetFile clone() {
        try {
            return (SpreadsheetFile) super.clone();
        } catch (Throwable t) {
            throw new InternalError(t.toString());
        }
    }

}

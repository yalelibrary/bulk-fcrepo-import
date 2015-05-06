package edu.yale.library.ladybird.engine.imports;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class Spreadsheet implements Cloneable {

    private String fileName;

    private String path;

    private InputStream fileStream;

    public Spreadsheet(String fileName, String path, InputStream fileStream) {
        this.fileName = fileName;
        this.path = path;
        this.fileStream = fileStream;
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
                + '}';
    }

    @Override
    protected Spreadsheet clone() {
        try {
            return (Spreadsheet) super.clone();
        } catch (Throwable t) {
            throw new InternalError(t.toString());
        }
    }

}

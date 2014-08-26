package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Write Processor. Subject to modification.
 */
public class ExportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Writes out Excel file to disk
     * @param list content
     * @param filePath path to write to
     * @throws IOException
     */
    public void write(final List<ImportEntity.Row> list, final String filePath) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet("Default Sheet");
        int rowNum = 0;
        for (final ImportEntity.Row importRow : list) {
            final Row row = sheet.createRow(rowNum++);
            final List<ImportEntity.Column> columnList = importRow.getColumns();
            int cellNum = 0;
            for (Object o : columnList) {
                final ImportEntity.Column col = (ImportEntity.Column) o;
                final String colValue = col.getValue().toString();
                final Cell cell = row.createCell(cellNum++);

                if (colValue instanceof String) {
                    cell.setCellValue(colValue);
                } else {
                    logger.debug("Unknown col data type={}", colValue);
                }
            }
        }
        /* Write contents */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
            logger.debug("Wrote Excel file={}", filePath);
        } catch (IOException e) {
            logger.error("Error writing spreadsheet", e);
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error("Error closing file stream", e);
            }
        }
    }

    public void writeSheets(final List<ExportSheet> exportSheets, final String filePath) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();

        for (ExportSheet exportSheet: exportSheets) {
            logger.debug("Writing sheet={}", exportSheet.getTitle());

            final XSSFSheet sheet = workbook.createSheet(exportSheet.getTitle());
            int rowNum = 0;
            for (final ImportEntity.Row importRow : exportSheet.getContents()) {
                final Row row = sheet.createRow(rowNum++);
                final List<ImportEntity.Column> columnList = importRow.getColumns();
                int cellNum = 0;
                for (Object o : columnList) {
                    final ImportEntity.Column col = (ImportEntity.Column) o;
                    final String colValue = col.getValue().toString();
                    final Cell cell = row.createCell(cellNum++);

                    if (colValue instanceof String) {
                        cell.setCellValue(colValue);
                    } else {
                        logger.debug("Unknown col data type={}", colValue);
                    }
                }
            }
        }

        try {
            writeFile(workbook, filePath);
        } catch (Exception e) {
            throw e;
        }
    }

    public void writeFile(XSSFWorkbook workbook, String filePath) throws IOException {
         /* Write contents */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
            logger.debug("Wrote Excel file={}", filePath);
        } catch (IOException e) {
            logger.error("Error writing spreadsheet", e);
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error("Error closing file stream", e);
            }
        }
    }

}

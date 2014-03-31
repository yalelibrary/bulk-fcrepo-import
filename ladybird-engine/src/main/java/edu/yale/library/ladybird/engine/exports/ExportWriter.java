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

    public void write(final List<ImportEntity.Row> list, final String filePath) throws IOException {
        /* Create rows and cells */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Default Sheet");
        int rowNum = 0;
        for (ImportEntity.Row importRow : list) {
            //logger.debug("Creating xls row=" + rowNum);
            Row row = sheet.createRow(rowNum++);
            List<ImportEntity.Column> columnList = importRow.getColumns();
            int cellNum = 0;
            for (Object o : columnList) {
                ImportEntity.Column col = (ImportEntity.Column) o;
                String colValue = col.getValue().toString();
                Cell cell = row.createCell(cellNum++);

                if (colValue instanceof String) {
                    cell.setCellValue(colValue);
                    //logger.debug("Added cell=" + cellNum);
                } else {
                    logger.debug("Unknown col data type.");
                }
            }
        }
        /* Write contents */
        try {
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
            logger.debug("Wrote Excel file=" + filePath);
        } catch (IOException e) {
            logger.error("Error writing spreadsheet", e);
            throw e;
        }
    }

}

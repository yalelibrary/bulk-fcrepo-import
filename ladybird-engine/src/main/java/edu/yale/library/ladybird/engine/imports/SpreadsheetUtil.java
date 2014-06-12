package edu.yale.library.ladybird.engine.imports;

import org.apache.poi.ss.usermodel.Cell;

/**
 *
 */
public class SpreadsheetUtil {
    /**
     * Helper
     *
     * Returns cell value as an object
     *
     * @param cell preadsheet cell
     * @return Object wrapping primitive or string
     */
    static Object getCellValue(final Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return (int) cell.getNumericCellValue(); //TODO note int. FIXME
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                throw new IllegalArgumentException("Unknown data type");
        }
    }
}

package edu.yale.library.ladybird.engine.imports;

import org.apache.poi.ss.usermodel.Cell;

public class PoiCellConverter {
    /**
     * Helper returns cell value as an object
     *
     * @param cell preadsheet cell
     * @return Object wrapping primitive or string
     */
    static Object getCellValue(final Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return (long) cell.getNumericCellValue(); //TODO note int. FIXME
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BLANK:
                return "";
            default:
                throw new IllegalArgumentException("Unknown data type:" + cell.getCellType());
        }
    }
}

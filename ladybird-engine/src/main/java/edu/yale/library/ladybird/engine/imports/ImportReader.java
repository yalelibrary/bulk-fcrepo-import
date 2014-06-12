package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.UnknownFieldConstantException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Spreadsheet reader.
 */
public final class ImportReader {

    private static final Logger logger = getLogger(ImportReader.class);

    private final SpreadsheetFile file;
    private final int sheetNumber; //assumes one sheet
    private ReadMode readMode;

    public ImportReader(SpreadsheetFile file, int sheetNumber, ReadMode readMode) {
        this.file = file;
        this.sheetNumber = sheetNumber;
        this.readMode = readMode;
    }

    /**
     * Process a sheet (NOTE: assumes default sheet 0)
     *
     * @return data structure containing all the data from the spreadsheet
     * @throws ImportReaderValidationException
     * @throws IOException
     */
    public List<ImportEntity.Row> processSheet() throws ImportReaderValidationException, IOException {

        logger.debug("Processing sheet of file={}", file);

        final List<ImportEntity.Row> sheetRows = new ArrayList<>();
        final List<FieldConstant> valueMap = new ArrayList<>();

        try {
            final XSSFSheet sheet = getDefaultSheet();
            final Iterator<Row> it = sheet.iterator();

            //read first row
            final Row firstRow = it.next();
            final Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();
            final ImportEntity.Row headerSheetRow = new ImportEntity().new Row();

            while (firstRowCellIterator.hasNext()) {
                final Cell cell = firstRowCellIterator.next();

                // Reader Header value.
                try {
                    FieldConstant f = getFieldConstant(String.valueOf(cellValue(cell)));

                    valueMap.add(f);

                    final ImportEntity.Column<String> column = new ImportEntity()
                            .new Column<>(f, String.valueOf(cellValue(cell)));
                    headerSheetRow.getColumns().add(column);
                } catch (UnknownFieldConstantException unknownFunction) {
                    if (this.readMode == ReadMode.HALT) {
                       logger.error("Unknown field column in header= {}", unknownFunction.getMessage());
                       final ImportReaderValidationException importReaderValidationException =
                               new ImportReaderValidationException(unknownFunction);
                        importReaderValidationException.initCause(unknownFunction);
                        throw importReaderValidationException;
                    }
                    logger.debug("Unknown exhead= {}", unknownFunction.getMessage()); //means probably fdid has not been added in the properties
                    valueMap.add(FunctionConstants.UNK); //TODO shouldn't be used to represent both unknown func and fdid

                    logger.debug("Adding UNK in 1st row for this unrecognized FieldConstant"); //added to keep the exhead and contents col. the same.

                    final ImportEntity.Column<String> column = new ImportEntity()
                            .new Column<>(FunctionConstants.UNK, String.valueOf(cellValue(cell)));
                    logger.trace("1st row Column={}", column.toString());
                    headerSheetRow.getColumns().add(column);

                } catch (Exception e) {
                    logger.error("Unknown error iterating header row", e);
                }
            }
            //add header row:
            sheetRows.add(headerSheetRow);

            logger.trace("Done iterating sheet exhead");
            logger.trace("Writing import content rows");

            //iterate body: //TODO Check empty columnns.
            int cellCount = 0;
            while (it.hasNext()) {
                final ImportEntity.Row contentsSheetRow = new ImportEntity().new Row();
                final Row row = it.next();
                final Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    final Cell cell = cellIterator.next();
                    final ImportEntity.Column<String> column = new ImportEntity().new Column<>(valueMap.get(cellCount),
                            String.valueOf(cellValue(cell)));
                    logger.trace("Column={}", column.toString());
                    contentsSheetRow.getColumns().add(column);
                    cellCount++;
                }
                logger.trace("Added content row={}", contentsSheetRow.toString());
                sheetRows.add(contentsSheetRow);
                cellCount = 0;
            }
        } catch (IOException e) {
            logger.error("Error reading value in import reading", e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.debug("Error reading cell", e); //ignore
        } catch (Exception e) {
            logger.error("General exception.", e); //ignore
        }
        logger.debug("Done import reading.");
        return sheetRows;
    }

    /**
     * Transform a strihng into a a FieldConstant
     *
     * @param cellValue spreadsheet cell value
     * @return a FieldConstant
     * @throws edu.yale.library.ladybird.engine.model.UnknownFieldConstantException
     * @see FunctionConstants
     */
    public static FieldConstant getFieldConstant(final String cellValue) throws UnknownFieldConstantException {

        FieldConstant f = FieldConstantRules.convertStringToFieldConstant(cellValue);
        if (f != null) {
            return f;
        }

        //try converting it to function constant (redundantly) FIXME

        try {
            final String normCellString = cellValue.replace("{", "").replace("}", "");
            final FieldConstant fieldConst = FunctionConstants.valueOf(normCellString.toUpperCase());
            return fieldConst;
        } catch (IllegalArgumentException e) {
            throw new UnknownFieldConstantException("Specified cell=" + cellValue + " not a recognized function or fdid.");
        }
    }

    /**
     * TODO change return type
     * Returns cell value as an object
     *
     * @param cell
     * @return Object wrapping primitive or string
     */
    private Object cellValue(final Cell cell) {
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

    /**
     * Read default sheet (0 for now)
     *
     * @return
     * @throws IOException
     */
    private XSSFSheet getDefaultSheet() throws IOException {
        logger.debug("Reading sheet={}", file.getFileName());
        final XSSFWorkbook workbook = new XSSFWorkbook(file.getFileStream());
        final XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
        logger.debug("Reading sheet done");
        return sheet;
    }
}

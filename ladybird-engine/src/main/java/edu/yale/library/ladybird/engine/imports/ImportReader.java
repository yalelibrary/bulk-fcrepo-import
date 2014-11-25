package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.UnknownFieldConstantException;
import edu.yale.library.ladybird.entity.FieldConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Spreadsheet reader.
 */
public final class ImportReader {

    private static final Logger logger = getLogger(ImportReader.class);

    private final SpreadsheetFile file;
    private final int sheetNumber;
    private ReadMode readMode;

    public ImportReader(SpreadsheetFile file, int sheetNumber, ReadMode readMode) {
        this.file = file;
        this.sheetNumber = sheetNumber;
        this.readMode = readMode;
    }

    /**
     * Process a single sheet SpreadsheetFile.sheetNumber of SpreadsheetFile file
     *
     * @return data structure containing all the data from the spreadsheet
     * @throws ImportReaderValidationException
     * @throws IOException
     */
    public List<ImportEntity.Row> read() throws ImportReaderValidationException, IOException {
        logger.debug("Processing sheetNumber={} of file={}", sheetNumber, file);

        final List<ImportEntity.Row> sheetRows = new ArrayList<>();
        final List<FieldConstant> valueMap = new ArrayList<>();

        try {
            final XSSFSheet sheet = file.getDefaultSheet(sheetNumber);
            final Iterator<Row> it = sheet.iterator();

            //read first row
            final Row firstRow = it.next();
            final Iterator<Cell> firstRowCellItr = firstRow.cellIterator();
            final ImportEntity.Row headerRow = new ImportEntity().new Row();

            int exHeadRowCellCount = 0;

            while (firstRowCellItr.hasNext()) {
                final Cell cell = firstRowCellItr.next();

                // Reader Header value.
                try {
                    FieldConstant f = FieldConstantUtil.getFieldConstant(String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    valueMap.add(f);

                    final ImportEntity.Column<String> column = new ImportEntity().new Column<>(f, String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    headerRow.getColumns().add(column);
                } catch (UnknownFieldConstantException unknownFunction) {
                    if (this.readMode == ReadMode.HALT) {
                        logger.error("Unknown field column in header={}", unknownFunction.getMessage());
                        final ImportReaderValidationException importReaderValidationException =
                                new ImportReaderValidationException(unknownFunction);
                        importReaderValidationException.initCause(unknownFunction);
                        throw importReaderValidationException;
                    }
                    logger.debug("Unknown exhead={}", unknownFunction.getMessage()); //means probably fdid has not been added in the properties

                    valueMap.add(FunctionConstants.UNK); //TODO shouldn't be used to represent both unknown func and fdid

                    logger.debug("Adding UNK in 1st row for this unrecognized FieldConstant"); //added to keep the exhead and contents col. the same.

                    final ImportEntity.Column<String> column = new ImportEntity()
                            .new Column<>(FunctionConstants.UNK, String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    headerRow.getColumns().add(column);
                } catch (Exception e) {
                    logger.error("Unknown error iterating header row", e);
                }

                exHeadRowCellCount++;
            }

            logger.trace("Exhead cell count={}", exHeadRowCellCount);

            //add header row:
            sheetRows.add(headerRow);

            //iterate body:
            int cellCount = 0;
            final ImportEntity importEntity = new ImportEntity();
            int debugRowCount = 0;

            try {
                while (it.hasNext()) {
                    //logger.trace("Reading content row={}", debugRowCount);
                    debugRowCount++;

                    final ImportEntity.Row contentsSheetRow = importEntity.new Row();
                    final Row row = it.next();
                    int lastColumn = Math.max(row.getLastCellNum(), exHeadRowCellCount);

                    logger.trace("Last column={}", lastColumn);

                    for (int cn = 0; cn < lastColumn && cn < exHeadRowCellCount; cn++) {
                        Cell cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);

                        //handle null fields, otherwise values will get mushed
                        if (cell == null) {
                            //logger.trace("Null field.");

                            contentsSheetRow.getColumns().add(ImportEntityValue.getBlankColumn(valueMap.get(cn)));
                            cellCount++;

                            //logger.trace("Added row");
                        } else {
                            final ImportEntity.Column<String> column = importEntity.new Column<>(valueMap.get(cellCount),
                                    String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                            //logger.trace("Column={}", column.toString());
                            contentsSheetRow.getColumns().add(column);
                            cellCount++;
                        }
                    }
                    ImportEntity.Row evalRow = new ImportEntity().new Row(Collections.unmodifiableList(contentsSheetRow.getColumns()));

                    if (!allFieldsNull(evalRow)) {
                        sheetRows.add(contentsSheetRow);
                    }

                    cellCount = 0;
                }
                //logger.trace("Content row index={}", debugRowCount);
            } catch (IllegalArgumentException e) {
                logger.error("Error reading cell column={}, error={}", cellCount + 1, e.getMessage());
                throw new IllegalArgumentException(e);
            }
        } catch (IOException e) {
            logger.error("Error reading value in import reading", e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Error reading cell value={}", e.getMessage()); //ignore
            throw e;
        }

        logger.debug("Done reading sheet.");
        return sheetRows;
    }

    private boolean allFieldsNull(final ImportEntity.Row row) {
        List<ImportEntity.Column> cols = row.getColumns();
        boolean blank = true;

        for (final ImportEntity.Column<String> c : cols) {
            if (c.getValue() == null) {
                logger.error("Null column value found");
                throw new IllegalArgumentException("Null col value");
            }

            if (!c.getValue().isEmpty()) {
                blank = false;
                return blank;
            }
        }
        return blank;
    }

}

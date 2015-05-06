package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalIdentifier represents a barcode or bibid along with some context info.
 * N.B.: The contextual fields can be removed once ImportEntityValue returns some sort of
 * context with value (as opposed to just plain string).
 *
 * @param <T>
 * @author Osman Din
 */
public class LocalIdentifier<T> {

    private static Logger logger = LoggerFactory.getLogger(LocalIdentifier.class);

    private T id;

    /** represent context for tablular data */
    private int row;

    /** represent context for tablular data */
    private int column;

    public LocalIdentifier(final T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(final T id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Converts a list of columns to a list of LocalIdentifier (barcode or bibids)
     * @param importValue representing list of columns
     * @return list of LocalIdentifier
     *
     * Note: relies heavily on importentityvalue implementation
     */
    public static List<LocalIdentifier<String>> getLocalIdList(ImportValue importValue) {
        int importSourceColumn = -1;

        List<String> list = importValue.getColumnStrings(FunctionConstants.F104);

        if (!list.isEmpty()) {
            importSourceColumn = importValue.getFunctionPosition(FunctionConstants.F104);
        }

        if (list.isEmpty()) { //try F105 TODO
            list = importValue.getColumnStrings(FunctionConstants.F105);

            if (!list.isEmpty()) {
                importSourceColumn = importValue.getFunctionPosition(FunctionConstants.F105);
            }
        }

        List<LocalIdentifier<String>> listLocalIds = new ArrayList<>();
        int rowNum = 0;

        for (final String s: list) {
            rowNum++;

            if (s == null || s.isEmpty()) { //TODO regex
                logger.trace("Ignoring={}", s);
                continue;
            }

            LocalIdentifier localIdentifier = new LocalIdentifier(s);
            localIdentifier.setRow(rowNum);
            localIdentifier.setColumn(importSourceColumn);

            listLocalIds.add(localIdentifier);
        }

        return listLocalIds;
    }

}

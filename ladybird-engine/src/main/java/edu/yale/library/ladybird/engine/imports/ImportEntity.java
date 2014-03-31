package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FieldConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents rows and columns.
 * <p/>
 * A bean for now. Subject to modification.
 */
public class ImportEntity {
    List<Row> sheetRows = new ArrayList<>();

    public class Column<T> {
        FieldConstant field;
        T value;

        public Column(FieldConstant field, T value) {
            this.field = field;
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public FieldConstant getField() {
            return field;
        }
    }

    public class Row {
        private List<Column> columns = new ArrayList<>();

        public List<Column> getColumns() {
            return columns;
        }

        @Deprecated
        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }
    }

    public List<Row> getSheetRows() {
        return sheetRows;
    }

}

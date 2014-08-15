package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.FieldConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents rows and columns.
 */
public class ImportEntity {
    /**
     * Column represents a spreadsheet cell
     * @param <T>
     */
    public class Column<T> {
        FieldConstant field;
        T value;

        public Column(final FieldConstant field, final T value) {
            this.field = field;
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public FieldConstant getField() {
            return field;
        }

        @Override
        public String toString() {
            return "Column{"
                    + "field=" + field
                    + ", value=" + value
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Column column = (Column) o;

            if (field != null ? !field.equals(column.field) : column.field != null) {
                return false;
            }
            if (value != null ? !value.equals(column.value) : column.value != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = field != null ? field.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    /**
     * Row represents a spreadsheet row
     */
    public class Row {
        private List<Column> columns = new ArrayList<>();

        public List<Column> getColumns() {
            return columns;
        }

        @Deprecated
        public void setColumns(final List<Column> columns) {
            this.columns = columns;
        }

        @Override
        public String toString() {
            return "Row{"
                    + "columns=" + columns
                    + '}';
        }

        public Row(final List<Column> columns) {
            this.columns = columns;
        }

        public Row() {
        }
    }

}

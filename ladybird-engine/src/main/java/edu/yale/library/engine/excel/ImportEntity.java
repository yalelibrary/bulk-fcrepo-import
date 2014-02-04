package edu.yale.library.engine.excel;

import edu.yale.library.engine.FieldConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A bean for now. Subject to modification.
 */
public class ImportEntity
{
    List<Row> sheetRows = new ArrayList();

    public class Column
    {
       public FieldConstants field;
       public String value;  //TODO change type

        public Column(FieldConstants field, String value)
        {
            this.field = field;
            this.value = value;
        }
    }

    public class Row
    {
        private List<Column> columns = new ArrayList();

        public List<Column> getColumns()
        {
            return columns;
        }

        public void setColumns(List<Column> columns)
        {
            this.columns = columns;
        }
    }

    public List<Row> getSheetRows()
    {
        return sheetRows;
    }

}

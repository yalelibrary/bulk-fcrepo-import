package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class HandleMinterTest extends AbstractDBTest {

    @Test
    public void shouldConstructHandle() {
        HandleMinter handleMinter = new HandleMinter();
        ImportEntityValue importEntityValue = handleMinter.write(getTestData());
        assert (importEntityValue.getRowList().size() == 2);
        assert (importEntityValue.getRowList().get(1).getColumns().size() == 2);
        assertEquals(importEntityValue.getRowList().get(1).getColumns().get(1).getValue(), "http://hdl.handle.net/10079/bibid/6848003");
    }

    private ImportEntityValue getTestData() {
        final List<ImportEntity.Column> columns = new ArrayList<>();

        columns.add(getColumn(FunctionConstants.F104, ""));
        final ImportEntity.Row row = getRow(columns);

        final List<ImportEntity.Column> listColumns = new ArrayList<>();
        listColumns.add(getColumn(FunctionConstants.F104, "6848003")); //bibid

        final ImportEntity.Row contentRow = getRow(listColumns);

        List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(row);
        spreadsheetRows.add(contentRow);

        final ImportEntityValue importEntityValue = new ImportEntityValue(spreadsheetRows);

        return importEntityValue;
    }

    private ImportEntity.Column getColumn(final FieldConstant f, final String value) {
        return new ImportEntity().new Column<>(f, value);
    }

    private ImportEntity.Row getRow(final List<ImportEntity.Column> columns) {
        ImportEntity.Row row = new ImportEntity().new Row();
        row.setColumns(columns);
        return row;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }
}

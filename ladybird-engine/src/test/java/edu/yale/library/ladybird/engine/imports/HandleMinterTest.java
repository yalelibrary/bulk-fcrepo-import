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
        ImportValue importValue = handleMinter.write(getTestData());
        assert (importValue.getRowList().size() == 2);
        assert (importValue.getRowList().get(1).getColumns().size() == 2);
        assertEquals(importValue.getRowList().get(1).getColumns().get(1).getValue(), "http://hdl.handle.net/10079/bibid/6848003");
    }

    private ImportValue getTestData() {
        final List<Import.Column> columns = new ArrayList<>();

        columns.add(getColumn(FunctionConstants.F104, ""));
        final Import.Row row = getRow(columns);

        final List<Import.Column> listColumns = new ArrayList<>();
        listColumns.add(getColumn(FunctionConstants.F104, "6848003")); //bibid

        final Import.Row contentRow = getRow(listColumns);

        List<Import.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(row);
        spreadsheetRows.add(contentRow);

        final ImportValue importValue = new ImportValue(spreadsheetRows);

        return importValue;
    }

    private Import.Column getColumn(final FieldConstant f, final String value) {
        return new Import().new Column<>(f, value);
    }

    private Import.Row getRow(final List<Import.Column> columns) {
        Import.Row row = new Import().new Row();
        row.setColumns(columns);
        return row;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        //super.stop();
    }
}

package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

/**
 *
 */
public class ExportWriterTest {

    @Test
    public void shouldWriteXlsFile() {
        final ExportWriter exportWriter = new ExportWriter();
        final String testFilePath = asTemp("temp-test.xlsx");
        try {
            exportWriter.write(getData(), testFilePath);
        } catch (IOException e) {
            fail("Failed in writing sheet");
        }
    }

    private static String asTemp(String s) {
        return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + s;
    }

    private static List<ImportEntity.Row> getData() {
        final ImportEntity.Row row = new ImportEntity().new Row();
        final FieldConstant f1 = FunctionConstants.F104;
        final ImportEntity.Column<String> column1 = new ImportEntity().new Column<>(f1,
                String.valueOf("2222"));
        row.getColumns().add(column1);
        final FieldConstant f2 = FunctionConstants.F1;
        final ImportEntity.Column<String> column2 = new ImportEntity().new Column<>(f2,
                String.valueOf("2222"));
        row.getColumns().add(column2);
        return Collections.singletonList(row);
    }
}

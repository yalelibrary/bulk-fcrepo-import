package edu.yale.library.ladybird.engine.file;

import edu.yale.library.ladybird.engine.exports.ExportSheet;
import edu.yale.library.ladybird.engine.exports.ExportWriter;
import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

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

    @Test
    public void shouldWriteMultipleSheets() {
        final ExportWriter exportWriter = new ExportWriter();
        final String testFilePath = asTemp("temp-test2.xlsx");

        System.out.println("path" + testFilePath);

        ExportSheet exportSheet = new ExportSheet();
        exportSheet.setTitle("Sheet 1");
        exportSheet.setContents(getData());

        ExportSheet exportSheet2 = new ExportSheet();
        exportSheet2.setTitle("Sheet 2");
        exportSheet2.setContents(getData());

        List<ExportSheet> list = new ArrayList<>();
        list.add(exportSheet);
        list.add(exportSheet2);

        try {
            exportWriter.writeSheets(list, testFilePath);
        } catch (IOException e) {
            fail("Failed in writing sheet");
        }
    }

    private static String asTemp(String s) {
        return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + s;
    }

    private static List<Import.Row> getData() {
        final Import.Row row = new Import().new Row();
        final FieldConstant f1 = FunctionConstants.F104;
        final Import.Column<String> column1 = new Import().new Column<>(f1,
                String.valueOf("2222"));
        row.getColumns().add(column1);
        final FieldConstant f2 = FunctionConstants.F1;
        final Import.Column<String> column2 = new Import().new Column<>(f2,
                String.valueOf("2222"));
        row.getColumns().add(column2);
        return Collections.singletonList(row);
    }
}

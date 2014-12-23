package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TestUtil {
    public static Spreadsheet getImportSpreadsheeet(String file) {
        return new SpreadsheetFileBuilder().filepath(file).stream(getStream(file)).create();
    }

    public static Spreadsheet getExportSpreadsheeet(String file) throws FileNotFoundException {
        final String path = getProp("java.io.tmpdir") + getProp("file.separator") + file;

        return new SpreadsheetFileBuilder().filename("test_export_xlsx").filepath(path)
                .stream(new FileInputStream(path)).create();
    }

    public static InputStream getExportFileInuptStream(String file) throws FileNotFoundException {
        final String path = getProp("java.io.tmpdir") + getProp("file.separator") + file;
        return new FileInputStream(path);
    }

    public static String getProp(String prop) {
        return System.getProperty(prop);
    }

    public static String asTmp(final String s) {
        return getProp("java.io.tmpdir") + getProp("file.separator") + s;
    }

    public static InputStream getStream(String path) {
        return TestUtil.class.getClassLoader().getResourceAsStream(path);
    }

}

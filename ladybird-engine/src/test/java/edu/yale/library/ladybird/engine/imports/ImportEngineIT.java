package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests full cycle for read/write import/export.
 */
public class ImportEngineIT extends AbstractDBTest {

    @Deprecated
    private static final String XLS_FILE_TO_WRITE = asTmp("test_export.xlsx");

    /**
     * Full cycle read write
     *
     * @throws Exception
     */
    @Ignore("todo")
    @Test
    public void shouldRunFullCycle() throws Exception {

        KernelBootstrap kernelBootstrap = new KernelBootstrap();
        kernelBootstrap.setAbstractModule(new TestModule());

        ExportBus exportBus = new ExportBus();
        exportBus.setAbstractModule(new TestModule());

        //initFdids(); //TODO tmp. Inst app. rules for test (since db state is cleaned)

        final ImportEngine importEngine = new DefaultImportEngine(0, 1); //TODO chek params logic
        importEngine.setImportSourceProcessor(new ImportSourceProcessor()); //TODO

        final List<Import.Row> rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());

        assertEquals("Rows size mismatch", rows.size(), FileConstants.ROW_COUNT);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), 31);
        assertEquals("Column value mismatch", rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");

        final int imid = importEngine.write(rows);

        /* Add request for export */
        final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
        ExportEngineQueue.addJob(exportEvent);

        //Now read back:

        //The job itself:
        final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
        final List<ImportJob> importJobList = importJobHibernateDAO.findAll();
        assertEquals("Import job count mismatch", importJobList.size(), 1);

        //Headers (aka exhead):
        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();

        assertEquals("Import job exhead count mismatch", jobExheads.size(), FileConstants.COL_COUNT);
        assertEquals("Exhead count per imid mismatch}", importJobExheadDAO.getNumEntriesPerImportJob(imid), 32);

        //Contents (aka imjobcontents):
        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        final List<ImportJobContents> importJobContents = importJobContentsDAO.findAll();

        /* assert that data written to DB equals rows times cols (-1 for each since exhead is row 0 and F1 is col 1)
           from the spreadsheet. */

//        assertEquals("Import job contents size mismatch. ", importJobContents.size(),
//                (FileConstants.ROW_COUNT - 1) * (FileConstants.COL_COUNT - 1));

        /* Test Export */
        final ExportEngine exportEngine = new DefaultExportEngine();

        final ImportEntityContext importEntityContext = exportEngine.read();

        final List<Import.Row> listExportRows = importEntityContext.getImportJobList();

        assert (listExportRows != null);
        assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 78);

        //write this spreadsheet
        exportEngine.write(listExportRows, XLS_FILE_TO_WRITE);

        //again, read back:
        final List<Import.Row> rowsReadBack = importEngine.read(getExportSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        assertEquals("Rows size mismatch", rowsReadBack.size(), 78);
    }


    public Spreadsheet getImportSpreadsheeet() {
        return new SpreadsheetFileBuilder().filename(FileConstants.TEST_XLS_FILE)
                .filepath(FileConstants.TEST_XLS_FILE)
                .stream(getClass().getClassLoader().getResourceAsStream(FileConstants.TEST_XLS_FILE))
                .create();
    }

    public Spreadsheet getExportSpreadsheeet() throws FileNotFoundException {
        final String testPath = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator") + "test_export.xlsx";
        return new SpreadsheetFileBuilder().filename("test_export_xlsx")
                .filepath(testPath)
                .stream(new FileInputStream(testPath))
                .create();
    }

    /**
     * Test file constants
     */
    private class FileConstants {
        static final String TEST_XLS_FILE = "4654-pt1-READY-FOR-INGEST-A.xlsx";
        static final int ROW_COUNT = 78;
        static final int COL_COUNT = 32;
    }

    @Deprecated
    private static String asTmp(final String s) {
        return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + s;
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

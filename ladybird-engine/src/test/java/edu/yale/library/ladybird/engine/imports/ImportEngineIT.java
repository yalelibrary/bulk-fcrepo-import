package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.EventBus;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.cron.queue.ImportContextQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.kernel.ApplicationBootstrap;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static java.lang.System.getProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests full cycle for read/write import/export.
 */
public class ImportEngineIT extends AbstractDBTest {

    private static final String XLS_EXPORT_FILE = asTmp("test_export.xlsx");

    private static final String XLS = "4654-pt1-READY-FOR-INGEST-A.xlsx";

    private static final int ROWS = 78;

    private static final int COLS = 32;

    /**
     * Full cycle read write
     *
     * @throws Exception
     */
    @Test
    public void shouldRunFullCycle() throws Exception {
        ApplicationBootstrap applicationBootstrap = new ApplicationBootstrap();
        applicationBootstrap.setAbstractModule(new TestModule());

        EventBus eventBus = new EventBus();
        eventBus.setAbstractModule(new TestModule());

        final ImportEngine importEngine = new DefaultImportEngine(0, 1); //TODO param chk
        importEngine.setImportSourceProcessor(new ImportSourceProcessor()); //TODO

        final List<Import.Row> rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL);

        assertEquals("Rows size mismatch", rows.size(), ROWS);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), 31);
        assertEquals("Column value mismatch", rows.get(1).getColumns().get(4).getValue(),
                "Gilchrist, Scott");

        final int imid = importEngine.write(rows);

        /* Add request for export */
        final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
        ImportContextQueue.addJob(exportEvent);

        //Now read back:

        //The job itself:
        final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
        final List<ImportJob> importJobList = importJobHibernateDAO.findAll();
        assertEquals("Import job count mismatch", importJobList.size(), 1);

        //Headers (aka exhead):
        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();

        assertEquals("Import job exhead count mismatch", jobExheads.size(), COLS);
        assertEquals("Exhead count per imid mismatch}",
                importJobExheadDAO.getNumEntriesPerImportJob(imid), 32);

        //Contents (aka imjobcontents):
        /* assert that data written to DB equals rows times cols
        (-1 for each since exhead is row 0 and F1 is col 1)
           from the spreadsheet.

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        final List<ImportJobContents> importJobContents = importJobContentsDAO.findAll();
        assertEquals("Import job contents size mismatch. ", importJobContents.size(),
                (FileConstants.ROWS - 1) * (FileConstants.COLS - 1));
        */

        /* Test Export */
        final ExportEngine exportEngine = new DefaultExportEngine();
        final ImportContext importContext = exportEngine.read();

        final List<Import.Row> listExportRows = importContext.getImportRowsList();
        assertNotNull(listExportRows);
        assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 78);

        //write spreadsheet
        exportEngine.write(listExportRows, XLS_EXPORT_FILE);

        //again, read back:
        final List<Import.Row> rowsReadBack = importEngine.read(getExportSpreadsheeet(), ReadMode.FULL);
        assertEquals("Rows size mismatch", rowsReadBack.size(), 78);
    }


    public Spreadsheet getImportSpreadsheeet() {
        return new SpreadsheetFileBuilder().filename(XLS).filepath(XLS)
                .stream(getClass().getClassLoader().getResourceAsStream(XLS))
                .create();
    }

    public Spreadsheet getExportSpreadsheeet() throws FileNotFoundException {
        final String testPath = getProperty("java.io.tmpdir") + File.separator + "test_export.xlsx";
        return new SpreadsheetFileBuilder().filename("test_export_xlsx").filepath(testPath)
                    .stream(new FileInputStream(testPath)).create();
    }

    @Deprecated
    private static String asTmp(final String s) {
        return getProperty("java.io.tmpdir") + File.separator + s;
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

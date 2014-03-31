package edu.yale.library.ladybird.tests;

import edu.yale.library.ladybird.kernel.ServicesManager;
import edu.yale.library.ladybird.kernel.beans.ImportJob;
import edu.yale.library.ladybird.kernel.beans.ImportJobContents;
import edu.yale.library.ladybird.kernel.beans.ImportJobExhead;
import edu.yale.library.ladybird.kernel.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.kernel.dao.ImportJobDAO;
import edu.yale.library.ladybird.kernel.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.kernel.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.kernel.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.kernel.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEngine;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.model.ReadMode;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.DefaultFieldDataValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests full cycle for read/write import/export.
 */
public class ImportEngineIT {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServicesManager servicesManager;

    /* Contains test fdids corresponding to test excel file (instead of via db) */
    private static final String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    @Deprecated
    private static final String XLS_FILE_TO_WRITE = asTmp("test_export.xlsx");

    /**
     * Full cycle read write
     *
     * @throws Exception
     */
    @Test
    public void execute() throws Exception {
        //start the engine
        servicesManager.startDB(); //TODO

        setApplicationData(); //TODO tmp. Inst app. rules for test (since db state is cleaned)

        final ImportEngine importEngine = new DefaultImportEngine();

        logger.debug("Reading test spreadsheet rows");
        final List<ImportEntity.Row> rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        logger.debug("Read spreadseet rows");

        assertEquals("Rows size mismatch", rows.size(), FileConstants.ROW_COUNT);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), FileConstants.COL_COUNT);
        assertEquals("Column value mismatch", rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");

        logger.debug("Writing rows to tables");
        final int imid = importEngine.write(rows);

        //Note: This needs to be re-visited per logic requirement
            /* Add request for export */
        final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
        ExportEngineQueue.addJob(exportEvent);

        logger.debug("Added event=" + exportEvent.toString());

        //Now read back:

        //Job itself:
        final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
        final List<ImportJob> l = importJobHibernateDAO.findAll();
        assertEquals("Import job count mismatch", l.size(), 1);

        //Headers (aka exhead):
        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();

        assertEquals("Import job exhead count mismatch", jobExheads.size(), FileConstants.COL_COUNT);
        assertEquals("Exhead count per imid mismatch}", importJobExheadDAO.getNumEntriesPerImportJob(imid), 31);

        //Contents (aka imjobcontents):
        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        final List<ImportJobContents> importJobContents = importJobContentsDAO.findAll();

        /* assert that data written to DB equals rows times cols (-1 for each since exhead is row 0 and F1 is col 1)
           from the spreadsheet. */

        assertEquals("Import job contents size mismatch. ", importJobContents.size(),
                (FileConstants.ROW_COUNT - 1) * (FileConstants.COL_COUNT - 1));

        /* Test Export */
        final ExportEngine exportEngine = new DefaultExportEngine();

        logger.debug("Export engine reading import tables");
        final List<ImportEntity.Row> listExportRows = exportEngine.read();

        assert (listExportRows != null);
        logger.debug("Size={}", listExportRows.size());
        assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 76); //fixme

        //write this spreadsheet
        exportEngine.write(listExportRows, XLS_FILE_TO_WRITE);

        //again, read back:
        logger.debug("Reading the new test spreadsheet created by ExportEngine with ImportEngine");
        final List<ImportEntity.Row> rowsReadBack = importEngine.read(getExportSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        assertEquals("Rows size mismatch", rowsReadBack.size(), 76); //fixme
    }

    public SpreadsheetFile getImportSpreadsheeet() {
        SpreadsheetFile file = new SpreadsheetFile(FileConstants.TEST_XLS_FILE, "Test spreadsheet",
                FileConstants.TEST_XLS_FILE, getClass().getClassLoader().getResourceAsStream(FileConstants.TEST_XLS_FILE));
        return file;
    }

    public SpreadsheetFile getExportSpreadsheeet() throws FileNotFoundException {
        final String testPath = System.getProperty("user.home")
                + System.getProperty("file.separator") + "test_export.xlsx";
        SpreadsheetFile file = new SpreadsheetFile("test_export_xlsx", "Test export xls",
                testPath, new FileInputStream(testPath));
        return file;
    }

    /**
     * Sets business logic data
     */
    public void setApplicationData() {
        initFieldDefMap(); //set default fdids
    }

    /**
     * Inits fdids
     */
    public void initFieldDefMap() {
        try {
            new FieldDefinitionValue().setFieldDefMap(getTextFieldDefsMap());
        } catch (IOException | NullPointerException e) {
            logger.error("Test fdids could not be loaded", e);
            fail();
        }
    }

    /**
     * Helps in initing fdids via a tex tfile
     *
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public Map<String, FieldConstant> getTextFieldDefsMap() throws IOException {
        Map<String, FieldConstant> fdidsMap = new HashMap<>();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            //TODO put Int as key?
            fdidsMap.put(properties.getProperty(fdidInt), getFdid(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt)));
        }

        //test file entries should equal the number of spreadsheet columns:
        assertEquals("Wrong number of test fdids", fdidsMap.size(),
                FileConstants.FDID_COL_COUNT);

        return fdidsMap;
    }

    public void setJdbcFieldDefsMap() {
        //TODO (set fdid in test db?)
    }

    public FieldDefinitionValue getFdid(int fdid, String s) {
        return new FieldDefinitionValue(fdid, s);
    }

    /**
     * Test file constants
     */
    public class FileConstants {

        static final String TEST_XLS_FILE = "4654-pt1-READY-FOR-INGEST-A.xlsx";

        static final int ROW_COUNT = 78;

        static final int COL_COUNT = 31; //Actual number

        static final int FDID_COL_COUNT = 30; //COL_COUNT (regular fdids) minus a FunctionConstants (F1)

    }

    /* Creates the file in the user home directory. */
    @Deprecated
    private static String asTmp(final String s) {
        return System.getProperty("user.home") + System.getProperty("file.separator") + s;
    }

    @Before
    public void init() {
        servicesManager = new ServicesManager();
    }

    @After
    public void stopDB() throws SQLException {
        servicesManager.stopDB();
    }

}

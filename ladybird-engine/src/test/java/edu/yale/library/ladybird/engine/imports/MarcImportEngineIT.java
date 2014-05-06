package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.Util;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests full cycle for read/write import/export w/ F104 (OAI-PMH marc import).
 */
public class MarcImportEngineIT extends AbstractDBTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /* Contains test fdids corresponding to test excel file (instead of via db) */
    private static final String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    /**
     * Marc fdid test file constants
     */
    private static class MarcFdidMappingConstants {
        static final String TEST_XLS_FILE = "field-marc-mappings.xlsx";
        static final int TEST_SHEET_NUM = 0;
    }

    /**
     * Export file constants
     */
    private static class ExportFileConstants {
        static final String TEST_XLS_FILE = "marc-ingest.xlsx"; //input file
        static final int TEST_SHEET_NUM = 0; //TODO
        static final int ROW_COUNT = 5;
        static final int COL_COUNT = 1;
        static final int TEST_FDID_COUNT = 30;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stopDB() throws SQLException {
        super.stop();
    }

    /**
     * Full cycle read write
     *
     * @throws Exception
     */
    @Test
    public void execute() throws Exception {
        //start the engine

        KernelBootstrap kernelBootstrap = new KernelBootstrap();
        kernelBootstrap.setAbstractModule(new TestModule());

        setApplicationData(); //TODO tmp. Inst app. rules for test (since db state is cleaned)

        /* 1. read the marc fdid mapping */
        final MarcFdidSpreadsheetReader reader = new MarcFdidSpreadsheetReader();
        final List<FieldMarcMapping> fieldMarcMappingList = reader.readMarcMapping(MarcFdidMappingConstants.TEST_XLS_FILE,
                MarcFdidMappingConstants.TEST_SHEET_NUM);

        /* 2. read the F104 or F105 spreadsheet */
        final ImportEngine importEngine = new DefaultImportEngine();

        logger.debug("Reading test spreadsheet rows");
        final List<ImportEntity.Row> rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        logger.debug("Read oai-marc spreadseet rows");

        assertEquals("Rows size mismatch", rows.size(), ExportFileConstants.ROW_COUNT);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), ExportFileConstants.COL_COUNT);

        //kick off import writer
        //note: oai reading and writing is done at this time

        OaiProvider provider = new OaiProvider("id", getProp("oai_test_url_prefix"),
                getProp("oai_url_id"));
        importEngine.setOaiProvider(provider);

        int imid = importEngine.write(rows);

        //
        /* Add request for export */
        final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
        ExportEngineQueue.addJob(exportEvent);

        logger.debug("Added event=" + exportEvent.toString());

        //Now read back:

        //The job itself:
        final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
        final List<ImportJob> importJobList = importJobHibernateDAO.findAll();
        //assertEquals("Import job count mismatch", importJobList.size(), 2); //FIXME purge DB before test

        //Headers (aka exhead):
        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();

        /* Test Export */
        final ExportEngine exportEngine = new DefaultExportEngine();

        logger.debug("Export engine reading import tables");
        final List<ImportEntity.Row> listExportRows = exportEngine.read();

        assert (listExportRows != null);
        logger.debug("Size={}", listExportRows.size());
        //logger.debug(listExportRows.toString());
        for (ImportEntity.Row importRow: listExportRows) {
            //logger.debug("Col size={}", importRow.getColumns().size());
            List<ImportEntity.Column> col = importRow.getColumns();
            //for (ImportEntity.Column c: col) {
                //logger.debug("Col val={}", c.toString());
            //}

        }
        //assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 3);
        //FIXME 3 cuz only 880 read
    }

    /**
     * Sets business logic data
     */
    private void setApplicationData() {
        initFieldDefMap(); //set default fdids
    }

    /**
     * Init fdids
     */
    private void initFieldDefMap() {
        try {
            new FieldDefinitionValue().setFieldDefMap(getTextFieldDefsMap());
        } catch (IOException | NullPointerException e) {
            logger.error("Test fdids could not be loaded", e);
            fail();
        }
    }

    /**
     * Helps in initing fdids via a text tfile
     *
     * @return
     * @throws java.io.IOException
     * @throws NullPointerException
     * @see #FDID_TEST_PROPS_FILE
     */
    private Map<String, FieldConstant> getTextFieldDefsMap() throws IOException {
        final Map<String, FieldConstant> fdidsMap = new HashMap<>();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            fdidsMap.put(properties.getProperty(fdidInt), getFdid(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt)));
        }

        //test file entries should equal the number of spreadsheet columns:
        assertEquals("Wrong number of test fdids", fdidsMap.size(),
                ExportFileConstants.TEST_FDID_COUNT);

        return fdidsMap;
    }

    //TODO remove
    private FieldDefinitionValue getFdid(final int fdid, final String s) {
        return new FieldDefinitionValue(fdid, s);
    }

    /**
     * TODO refactor
     * Utility to create SpreadsheetFile
     * @return a SpreadsheetFile instance
     * @see edu.yale.library.ladybird.engine.imports.SpreadsheetFile
     */
    private SpreadsheetFile getImportSpreadsheeet() {
        final SpreadsheetFile file = new SpreadsheetFileBuilder().setFileName(ExportFileConstants.TEST_XLS_FILE)
                .setAltName("Test spreadsheet").setPath(ExportFileConstants.TEST_XLS_FILE)
                .setFileStream(getClass().getClassLoader().getResourceAsStream(ExportFileConstants.TEST_XLS_FILE))
                .createSpreadsheetFile();
        return file;
    }

    private String getProp(final String p) {
        return Util.getProperty(p);
    }

}

package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.Util;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;


/**
 * Tests full cycle for read/write import/export w/ F104 (OAI-PMH marc import).
 */
public class MarcBarcodeImportEngineIT extends AbstractDBTest {

    /**
     * Full cycle read write
     */
    @Test
    public void shouldRunFullCycle() {
        try {
            //start the engine
            KernelBootstrap kernelBootstrap = new KernelBootstrap();
            kernelBootstrap.setAbstractModule(new TestModule());

            initFdids(); //TODO tmp. Inst app. rules for test (since db state is cleaned)

            /* 1. read the marc fdid mapping */
            FdidMarcMappingUtil fdidMarcMappingUtil = new FdidMarcMappingUtil();
            fdidMarcMappingUtil.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO());
            fdidMarcMappingUtil.setInitialFieldMarcDb();
            final List<FieldMarcMapping> fieldMarcMappingList = new FieldMarcMappingHibernateDAO().findAll();

            assertEquals("FieldMarcMapping size mismatch", fieldMarcMappingList.size(), 86);

            /* 2. read the F104 or F105 spreadsheet */
            final ImportEngine importEngine = new DefaultImportEngine(0, 1); //TODO params logic

            importEngine.setImportSourceProcessor(new ImportSourceProcessor()); //TODO

            final List<ImportEntity.Row> rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL,
                    new DefaultFieldDataValidator());

            assertEquals("Rows size mismatch", rows.size(), ExportFileConstants.ROW_COUNT);
            assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), 5);

            //kick off import writer
            OaiProvider provider = new OaiProvider("id", getProp("oai_test_url_prefix_barcode"), getProp("oai_url_id"));
            importEngine.setOaiProvider(provider);

            int imid = importEngine.write(rows);

            /* Add request for export */
            final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
            ExportEngineQueue.addJob(exportEvent);

            //Now read back:

            //The job itself:
            final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
            final List<ImportJob> importJobList = importJobHibernateDAO.findAll();
            assert (importJobList.size() == 1);

            //Headers (aka exhead):
            final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
            final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();

            assertEquals(jobExheads.size(), 7);

            /* Test Export */
            final ExportEngine exportEngine = new DefaultExportEngine();

            final ImportEntityContext importEntityContext = exportEngine.read();

            final List<ImportEntity.Row> listExportRows = importEntityContext.getImportJobList();

            assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 2);

            ImportEntityValue importEntityValue = new ImportEntityValue(listExportRows); //to analyze this
            FieldConstant fieldConstant = new FieldDefinition(70, "");
            List<ImportEntity.Column> col = importEntityValue.getColumnValues(fieldConstant);

            //(Compare against an expected spreadhseet?)

            assertEquals(col.get(0).getValue(), "Early Tudor government, 1485-1558 /");

            //and so forth. . .

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    private void initFdids() {
        try {
            FieldDefinitionInitializer fieldDefinitionInitializer = new FieldDefinitionInitializer();
            fieldDefinitionInitializer.setInitialFieldDefinitionDb();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed");
        }
    }


    private SpreadsheetFile getImportSpreadsheeet() {
        return new SpreadsheetFileBuilder().filename(ExportFileConstants.TEST_XLS_FILE)
                .altname("Test spreadsheet").filepath(ExportFileConstants.TEST_XLS_FILE)
                .stream(getClass().getClassLoader().getResourceAsStream(ExportFileConstants.TEST_XLS_FILE))
                .create();
    }

    private String getProp(final String p) {
        return Util.getProperty(p);
    }

    /**
     * Export file constants
     */
    private static class ExportFileConstants {
        static final String TEST_XLS_FILE = "marc-ingest-barcode.xlsx";
        static final int ROW_COUNT = 2;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stopDB() throws SQLException {
        super.stop();
    }

}

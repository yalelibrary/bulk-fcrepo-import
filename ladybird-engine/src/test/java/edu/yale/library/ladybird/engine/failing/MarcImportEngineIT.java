package edu.yale.library.ladybird.engine.failing;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.EventBus;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.Util;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEngine;
import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportValue;
import edu.yale.library.ladybird.engine.imports.ImportReaderValidationException;
import edu.yale.library.ladybird.engine.imports.ReadMode;
import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests full cycle for read/write import/export w/ F104 (OAI-PMH marc import).
 */
public class MarcImportEngineIT extends AbstractDBTest {

    private Logger logger = LoggerFactory.getLogger(MarcImportEngineIT.class);

    /**
     * Full cycle read write
     */
    @Ignore("todo")
    @Test
    public void shouldRunFullCycle() {
        //start the engine
        KernelBootstrap kernelBootstrap = new KernelBootstrap();
        kernelBootstrap.setAbstractModule(new TestModule());

        EventBus eventBus = new EventBus();
        eventBus.setAbstractModule(new TestModule());

        /* 1. read the marc fdid mapping */
        FdidMarcMappingUtil fdidMarcMappingUtil = new FdidMarcMappingUtil();
        fdidMarcMappingUtil.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO());
        try {
            fdidMarcMappingUtil.setInitialFieldMarcDb();
        } catch (Exception e) {
            e.printStackTrace();  //TODO
        }
        final List<FieldMarcMapping> fieldMarcMappingList = new FieldMarcMappingHibernateDAO().findAll();

        assertEquals("FieldMarcMapping size mismatch", fieldMarcMappingList.size(), 86);

            /* 2. read the F104 or F105 spreadsheet */
        final ImportEngine importEngine = new DefaultImportEngine(0, 1); //chk params logic

        importEngine.setImportSourceProcessor(new ImportSourceProcessor()); //TODO

        List<Import.Row> rows = Collections.emptyList();
        try {
            rows = importEngine.read(getImportSpreadsheeet(), ReadMode.FULL,
                    new DefaultFieldDataValidator());
        } catch (ImportReaderValidationException e) {
            e.printStackTrace();  //TODO
        } catch (IOException e) {
            e.printStackTrace();  //TODO
        }

        assertEquals("Rows size mismatch", rows.size(), ExportFileConstants.ROW_COUNT);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), ExportFileConstants.ROW_COUNT);

        //kick off import writer
        OaiProvider provider = new OaiProvider("id", getProp("oai_test_url_prefix"), getProp("oai_url_id"));
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

        final List<Import.Row> listExportRows = importEntityContext.getImportJobList();

        assertEquals("Export rows don't equal import expected rows", listExportRows.size(), 5);

        ImportValue importValue = new ImportValue(listExportRows); //to analyze this
        FieldConstant fieldConstant = new FieldDefinition(70, "");
        List<Import.Column> col = importValue.getColumnValues(fieldConstant);

        //(Compare against an expected spreadhseet?)

        assert (col.size() == 4);
        assertEquals(col.get(0).getValue(), "1981-2005 :");
        assertEquals(col.get(1).getValue(), "Kitāb Fawāʼid ʻaẓīmah li-rabṭ al-ḥāḍir ʻalá al-māḍī,");
        assertEquals(col.get(2).getValue(), "Primavera silenziosa /");
        assertEquals(col.get(3).getValue(), "Programming with VisiBroker");

        //and so forth. . .

        logger.info("DONE");
    }


    private Spreadsheet getImportSpreadsheeet() {
        return new SpreadsheetFileBuilder().filename(ExportFileConstants.TEST_XLS_FILE)
                .filepath(ExportFileConstants.TEST_XLS_FILE)
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
        static final String TEST_XLS_FILE = "marc-ingest.xlsx";
        static final int ROW_COUNT = 5;
    }

    @Before
    public void init() {
        super.init();
        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();
        ObjectStringDAO osDAO = new ObjectStringHibernateDAO();
        ObjectStringVersionDAO osvDAO = new ObjectStringVersionHibernateDAO();
        ObjectAcidVersionDAO oavDAO = new ObjectAcidVersionHibernateDAO();
        ObjectDAO objectDAO = new ObjectHibernateDAO();

        authDAO.deleteAll();
        osvDAO.deleteAll();
        oavDAO.deleteAll();
        oaDAO.deleteAll();
        osDAO.deleteAll();
        objectDAO.deleteAll();
    }

    @After
    public void stop() throws SQLException {
        //super.stop();
    }

}

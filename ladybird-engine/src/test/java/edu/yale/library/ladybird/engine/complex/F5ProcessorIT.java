package edu.yale.library.ladybird.engine.complex;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.TestUtil;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.DefaultExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportEngine;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.imports.FieldDefinitionInitializer;
import edu.yale.library.ladybird.engine.imports.ImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.SpreadsheetUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Tests full cycle for read/write import/export for complex objects.
 */
public class F5ProcessorIT extends AbstractDBTest {

    private final Logger logger = getLogger(F5ProcessorIT.class);

    private static final String IMPORT_FILE = "F5-test.xlsx";

    private static final String EXPORT_FILE_NAME = "test_export.xlsx";

    private static final String EXPORT_FILE = TestUtil.asTmp(EXPORT_FILE_NAME);

    private class ShouldMatch {
        static final int ROW_COUNT = 5;
        static final int COL_COUNT = 4;
    }

    final KernelBootstrap kernelBoot = new KernelBootstrap();
    final ExportEngine exportEngine = new DefaultExportEngine();
    final FieldDefinitionInitializer fieldDefinitionInitializer = new FieldDefinitionInitializer();
    final ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
    final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
    final ObjectDAO objectDAO = new ObjectHibernateDAO();

    /**
     * Full cycle read write
     *
     * @throws Exception
     */
    @Test
    public void shouldRunFullCycleForComplex() throws Exception {
        kernelBoot.setAbstractModule(new TestModule());
        initFdids(); //TODO tmp. Inst app. rules for test (since db state is cleaned)
        int userId = 0, projectId = 1;

        final ImportEngine importEngine = new DefaultImportEngine(userId, projectId);
        importEngine.setImportSourceProcessor(new ImportSourceProcessor()); //TODO

        final List<ImportEntity.Row> rows = importEngine.read(TestUtil.getImportSpreadsheeet(IMPORT_FILE));

        assertEquals(rows.size(), ShouldMatch.ROW_COUNT);
        assertEquals(rows.get(0).getColumns().size(), ShouldMatch.COL_COUNT);

        //Write it:
        final int imid = importEngine.write(rows);

        /* Add request for export */
        final ExportRequestEvent exportEvent = new ExportRequestEvent(imid);
        ExportEngineQueue.addJob(exportEvent);

        //Now read back:

        //The job itself:
        final List<ImportJob> importJobList = importJobHibernateDAO.findAll();
        assertEquals(importJobList.size(), 1);

        final List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();
        assertEquals(jobExheads.size(), ShouldMatch.COL_COUNT + 2); //1 for f1, 1 for f111
        assertEquals(importJobExheadDAO.getNumEntriesPerImportJob(imid), ShouldMatch.COL_COUNT + 2);

        //final List<ImportJobContents> importJobContents = importJobContentsDAO.findAll();

        /* Test Export */
        final ImportEntityContext ieContext = exportEngine.read();

        final List<ImportEntity.Row> exportRowsList = ieContext.getImportJobList();
        assertEquals(exportRowsList.size(), ShouldMatch.ROW_COUNT);

        //write this spreadsheet
        exportEngine.write(exportRowsList, EXPORT_FILE);

        //again, read back:
        final List<ImportEntity.Row> rowsReadBack = importEngine.read(TestUtil.getExportSpreadsheeet(EXPORT_FILE_NAME));
        assertEquals(rowsReadBack.size(), ShouldMatch.ROW_COUNT);

        //assert physical presence of F1 column
        int sheetNum = 0;
        List<String> colValues = columnValues(FunctionConstants.F1, EXPORT_FILE_NAME, sheetNum);
        assertEquals(colValues.size(), ShouldMatch.ROW_COUNT - 1);

        //verify that F1 values are the same as oids created

        List<Object> list = objectDAO.findByProject(projectId);

        logger.debug(list.toString());

        assert (list.size() == colValues.size());

        for (Object o : list) {
            assert (colValues.contains(String.valueOf(o.getOid())));
        }

        assert (objectDAO.findByParent(1).size() == 3);

        //and so on.. could test for other object tables, but most important for F5 is to generate F1
    }

    private List<String> columnValues(FunctionConstants f, String file, int sheetNum) throws Exception {
        try {
            return SpreadsheetUtil.getColumnValues(TestUtil.getExportFileInuptStream(file), f.getName(), sheetNum);
        } catch (Exception e) {
            throw e;
        }
    }

    private void initFdids() {
        try {
            fieldDefinitionInitializer.setInitialFieldDefinitionDb();
        } catch (IOException e) {
            logger.error("Error", e);
            fail("Failed");
        }
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

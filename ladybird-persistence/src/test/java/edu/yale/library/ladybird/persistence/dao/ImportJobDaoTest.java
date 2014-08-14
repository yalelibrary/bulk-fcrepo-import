package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ImportJobDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Before
    public void init() {
        initDB();
    }

    @After
    public void stop() throws SQLException {
        //TODO
    }

    @Inject
    private ImportJobDAO dao;

    @Test
    public void shouldSave() {
        final ImportJob item = build();
        List list = null;
        List jobDirList = null;
        List userJobList = null;
        try {
            dao.save(item);
            list = dao.findAll();
            jobDirList = dao.findByJobId(1);
            userJobList = dao.findByUser(0);
        } catch (Throwable e) {
            fail("Error testing saving or finding item" + e.getMessage());
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ImportJob imj = (ImportJob) list.get(0);
        assertEquals("Value mismatch", (long) imj.getImportId(), 1);
        assertEquals("Value mismatch", imj.getJobFile(), "file");
        assertEquals("Value mismatch", imj.getJobDirectory(), "dir");
        assertEquals("Value mismatch", imj.getExportJobFile(), "export-file");
        assertEquals("Value mismatch", imj.getExportJobDir(), "export-dir");

        final ImportJob imj2 = (ImportJob) jobDirList.get(0);
        assertEquals("Value mismatch", (long) imj2.getImportId(), 1);
        assertEquals("Value mismatch", imj2.getJobFile(), "file");
        assertEquals("Value mismatch", imj2.getJobDirectory(), "dir");
        assertEquals("Value mismatch", imj2.getExportJobFile(), "export-file");
        assertEquals("Value mismatch", imj2.getExportJobDir(), "export-dir");

        final ImportJob imj3 = (ImportJob) userJobList.get(0);
        assertEquals("Value mismatch", (long) imj3.getImportId(), 1);
        assertEquals("Value mismatch", imj3.getJobFile(), "file");
        assertEquals("Value mismatch", imj3.getJobDirectory(), "dir");
        assertEquals("Value mismatch", imj3.getExportJobFile(), "export-file");
        assertEquals("Value mismatch", imj3.getExportJobDir(), "export-dir");

        try {
            item.setExportJobFile("new-export-job-file");
            dao.saveOrUpdateItem(item);
            ImportJob updatedImportJob = dao.findAll().get(0);
            assert (updatedImportJob.getExportJobFile().equals("new-export-job-file"));
        } catch (Exception e) {
            fail("Error updating item" + e.getMessage());
        }

    }

    private ImportJob build() {
        final ImportJob item = new ImportJobBuilder()
                .setUserId(0).setJobDirectory("dir").setJobFile("file").setExportJobDir("export-dir").setExportJobFile("export-file").createImportJob();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
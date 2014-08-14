package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportFile;
import edu.yale.library.ladybird.entity.ImportFileBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ImportFileDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ImportFileDaoTest.class);

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
    private ImportFileDAO dao;

    @Test
    public void testSave() {
        final ImportFile item = build();
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ImportFile importFile = (ImportFile) list.get(0);
        assertEquals("Value mismatch", (long) importFile.getImportId(), 555);
    }

    private ImportFile build() {
        final ImportFile item = new ImportFileBuilder()
                .setImportId(555).setFileLocation("").createImportFile();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
package edu.yale.library.ladybird.persistence;

import edu.yale.library.ladybird.kernel.beans.ObjectFile;
import edu.yale.library.ladybird.kernel.beans.ObjectFileBuilder;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectFileDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ObjectFileDaoTest.class);

    {
        DaoInitializer.injectFields(this);
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
    private ObjectFileDAO dao;

    @Test
    public void testSave() {
        final ObjectFile item = build();
        List<ObjectFile> list = null;
        try {
            dao.save(item);
            list = dao.findAll();
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Value mismatch", list.get(0).getFileName(), "tmpFile");
    }

    private ObjectFile build() {
        final ObjectFile item = new ObjectFileBuilder().setFileName("tmpFile").createObjectFile();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
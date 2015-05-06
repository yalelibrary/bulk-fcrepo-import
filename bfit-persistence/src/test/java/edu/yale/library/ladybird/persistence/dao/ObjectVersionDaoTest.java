package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectVersionDaoTest extends AbstractPersistenceTest {

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
    private ObjectVersionDAO dao;

    @Test
    public void testSave() {
        final ObjectVersion item = new ObjectVersion();

        item.setUserId(1);
        item.setNotes("USER EDIT");
        item.setVersionId(1);
        item.setOid(2);

        List list = null;
        int maxVersion = -1;
        try {
            dao.save(item);
            list = dao.findByOid(2);
            maxVersion = dao.findMaxVersionByOid(2);

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ObjectVersion o = (ObjectVersion) list.get(0);
        assertEquals("Value mismatch", (long) o.getUserId(), 1);

        assert (maxVersion == 1);
    }

}
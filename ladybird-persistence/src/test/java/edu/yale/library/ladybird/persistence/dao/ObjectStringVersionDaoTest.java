package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectStringVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectStringVersionDaoTest extends AbstractPersistenceTest {

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
    private ObjectStringVersionDAO dao;

    @Test
    public void testSave() {
        final ObjectStringVersion item = new ObjectStringVersion();
        ObjectStringVersion obj1 = new ObjectStringVersion();

        item.setFdid(555);
        item.setValue("name");
        item.setVersionId(1);
        item.setDate(new Date());

        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();
            obj1 = dao.findByOidAndFdidAndVersion(0, 555, 1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ObjectStringVersion o = (ObjectStringVersion) list.get(0);
        assertEquals("Value mismatch", o.getFdid(), 555);
        assertEquals("Value mismatch", o.getValue(), "name");
        assertEquals(obj1.getValue(), "name");
    }

}
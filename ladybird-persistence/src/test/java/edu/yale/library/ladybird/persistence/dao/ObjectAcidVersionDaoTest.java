package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectAcidVersionDaoTest extends AbstractPersistenceTest {

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
    private ObjectAcidVersionDAO dao;

    @Test
    public void testSave() {
        final ObjectAcidVersion item = new ObjectAcidVersion();
        item.setFdid(555);
        item.setValue(66);
        item.setDate(new Date());

        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ObjectAcidVersion o = (ObjectAcidVersion) list.get(0);
        assertEquals("Value mismatch", o.getFdid(), 555);
        assertEquals("Value mismatch", o.getValue(), 66);
    }

}
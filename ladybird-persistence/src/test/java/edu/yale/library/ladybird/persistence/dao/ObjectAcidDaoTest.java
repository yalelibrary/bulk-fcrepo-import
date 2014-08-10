package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectAcidDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ObjectAcidDaoTest.class);

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
    private ObjectAcidDAO dao;

    @Test
    public void testSave() {
        final ObjectAcid item = new ObjectAcidBuilder().createObjectAcid();
        item.setFdid(69);
        item.setValue(777);
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
        final ObjectAcid o = (ObjectAcid) list.get(0);
        assertEquals("Value mismatch", o.getFdid(), 69);
        assertEquals("Value mismatch", o.getValue(), 777);
    }

}
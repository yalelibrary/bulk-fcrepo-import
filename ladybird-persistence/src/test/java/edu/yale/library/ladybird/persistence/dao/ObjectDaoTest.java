package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.kernel.model.Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ObjectDaoTest.class);

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
    private ObjectDAO dao;

    @Test
    public void testSave() {
        final Object item = build();
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Object o = (Object) list.get(0);
        assertEquals("Value mismatch", o.getProjectId(), 1);
    }

    private Object build() {
        final Object item = new Object(1);
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
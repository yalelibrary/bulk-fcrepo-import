package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AccessconditionProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AcessConditionProjectDaoTest extends AbstractPersistenceTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPersistenceTest.class);

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
    private AccessconditionProjectDAO dao;

    @Test
    public void testSave() {
        final AccessconditionProject item = new AccessconditionProject();
        item.setValue("test value");
        item.setDate(new Date());
        item.setUserId(0);
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final AccessconditionProject itemDB = (AccessconditionProject) list.get(0);
        assertEquals("Value mismatch", (long) itemDB.getUserId(), 0);
        assertEquals("Value mismatch", itemDB.getValue(), "test value");

    }

}

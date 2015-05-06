package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AccessconditionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AcessConditionTypeDaoTest extends AbstractPersistenceTest {

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
    private AccessconditionTypeDAO dao;

    @Test
    public void testSave() {
        final AccessconditionType item = new AccessconditionType();
        item.setLabel("label");
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
        final AccessconditionType itemDB = (AccessconditionType) list.get(0);
        assertEquals("Value mismatch", itemDB.getLabel(), "label");
        assertEquals("Value mismatch", itemDB.getUserId(), 0);
    }

}

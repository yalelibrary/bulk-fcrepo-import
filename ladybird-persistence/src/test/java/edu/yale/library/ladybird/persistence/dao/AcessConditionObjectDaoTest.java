package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AccessconditionObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AcessConditionObjectDaoTest extends AbstractPersistenceTest {

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
    private AccessconditionObjectDAO dao;

    @Test
    public void testSave() {
        //final AccessconditionObject item = new AccessconditionObject();
        final AccessconditionObject accessconditionObject = new AccessconditionObject();
        accessconditionObject.setOid(555);
        accessconditionObject.setValue("Random oid value");
        //item.setId(accessconditionObject);

        List list = null;
        try {
            dao.save(accessconditionObject);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final AccessconditionObject itemDB = (AccessconditionObject) list.get(0);
        assertEquals("Value mismatch", (long) itemDB.getOid(), 555);
        assertEquals("Value mismatch", itemDB.getValue(), "Random oid value");

    }

}


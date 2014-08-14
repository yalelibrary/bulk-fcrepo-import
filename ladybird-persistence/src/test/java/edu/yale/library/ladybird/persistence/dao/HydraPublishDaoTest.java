package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.HydraPublish;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class HydraPublishDaoTest extends AbstractPersistenceTest {

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
    private HydraPublishDAO hydraDAO;

    @Test
    public void testSave() {
        final HydraPublish hydra = new HydraPublish();
        hydra.setOid(6661);
        hydra.setDate(new Date());
        List list = null;
        try {
            hydraDAO.save(hydra);
            list = hydraDAO.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final HydraPublish item = (HydraPublish) list.get(0);
        assertEquals("Value mismatch", (long) item.getOid(), 6661);
    }


}
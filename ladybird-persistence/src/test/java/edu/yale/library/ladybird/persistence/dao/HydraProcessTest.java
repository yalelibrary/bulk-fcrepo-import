package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Hydra;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class HydraProcessTest extends AbstractPersistenceTest {

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
    private HydraDAO hydraDAO;

    @Test
    public void testSave() {
        final Hydra hydra = new Hydra();
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
        final Hydra item = (Hydra) list.get(0);
        assertEquals("Value mismatch", (long) item.getOid(), 6661);
    }


}
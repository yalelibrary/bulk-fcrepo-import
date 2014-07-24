package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Monitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class MonitorDaoTest extends AbstractPersistenceTest {

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
    private MonitorDAO dao;

    @Test
    public void shouldSave() {
        final Monitor item = new Monitor();
        item.setCurrentProjectId(1);
        item.setCurrentUserId(1);
        item.setExportPath("");
        item.setNotificationEmail("");
        List list = null;
        try {
            dao.save(item);
            list = dao.findByUserAndProject(1, 1);

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Monitor monitorItem = (Monitor) list.get(0);
        assertEquals("Value mismatch", (long) monitorItem.getCurrentProjectId(), 1);
    }

}
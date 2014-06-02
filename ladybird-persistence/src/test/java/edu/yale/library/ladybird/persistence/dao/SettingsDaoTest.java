package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SettingsDaoTest extends AbstractPersistenceTest {

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
    private SettingsDAO dao;

    @Test
    public void testSave() {
        final Settings item = new Settings();
        item.setProperty("M31 galaxy");
        item.setValue("andromeda");
        List list = null;

        Settings settings = null;
        try {
            dao.save(item);
            list = dao.findAll();

            settings = dao.findByProperty("M31 galaxy");

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Settings itemDB = (Settings) list.get(0);
        assertEquals("Value mismatch", itemDB.getValue(), "andromeda");

        assertEquals("Value mismatch", settings.getValue(), "andromeda");
    }
}
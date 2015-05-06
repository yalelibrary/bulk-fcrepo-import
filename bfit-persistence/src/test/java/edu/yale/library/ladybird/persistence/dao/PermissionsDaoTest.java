package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Permissions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PermissionsDaoTest extends AbstractPersistenceTest {

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
    private PermissionsDAO dao;

    @Test
    public void testSave() {
        final Permissions item = new Permissions("site.add", "Permission to save sites");
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Permissions permissions = (Permissions) list.get(0);
        assertEquals("Value mismatch", permissions.getPermissionsName(), "site.add");
    }
}
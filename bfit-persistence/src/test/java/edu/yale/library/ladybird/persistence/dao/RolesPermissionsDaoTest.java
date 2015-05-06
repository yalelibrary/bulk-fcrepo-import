package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.RolesPermissions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RolesPermissionsDaoTest extends AbstractPersistenceTest {

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
    private RolesPermissionsDAO dao;

    @Test
    public void shouldSave() {
        final RolesPermissions item = new RolesPermissions((short) 1, (short) 1, 'y');
        List list = null;
        RolesPermissions rolesPermissions = null;
        try {
            dao.save(item);
            list = dao.findAll();
             rolesPermissions = dao.findByRolesPermissionsId(1, 1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final RolesPermissions permissions = (RolesPermissions) list.get(0);
        assertEquals("Value mismatch", permissions.getRoleId(), 1);
        assertEquals("Value mismatch", (char) permissions.getValue(), 'y');
        assertEquals((char) rolesPermissions.getValue(), 'y');
    }
}
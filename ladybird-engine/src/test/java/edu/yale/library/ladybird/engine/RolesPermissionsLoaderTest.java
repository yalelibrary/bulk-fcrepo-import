package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesPermissionsHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.fail;

/**
 * FIXME move this elsewhere
 */
public class RolesPermissionsLoaderTest extends AbstractDBTest {

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stopDB() throws SQLException {
        super.stop();
    }
    @Test
    public void shouldWriteRolesandPermissions() {
        RolesPermissionsLoader rolesPermissionsLoader = new RolesPermissionsLoader();
        RolesPermissionsDAO rolesPermissionsDAO = new RolesPermissionsHibernateDAO();

        //Count roles and permissions:
        final int count = Roles.getRolesPermissionsSize();

        try {
            assert (rolesPermissionsDAO.findAll().size() == 0);
            rolesPermissionsLoader.load();
            assert (rolesPermissionsDAO.findAll().size() == count);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}

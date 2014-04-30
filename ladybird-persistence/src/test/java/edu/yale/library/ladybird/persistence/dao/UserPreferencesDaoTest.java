package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.UserPreferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class UserPreferencesDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserPreferencesDaoTest.class);

    {
        DaoInitializer.injectFields(this);
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
    private UserPreferencesDAO dao;

    @Test
    public void testSave() {
        List<UserPreferences> list = new ArrayList<>();
        final UserPreferences user = new UserPreferences();
        user.setProjectId(5);
        user.setUserId(1);
        try {
            dao.save(user);
            logger.debug("Saved item.");
            list = dao.findAll();
        } catch (Throwable e) {
            fail("Error testing saving or finding item");
        }
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Project value mismatch", (int) list.get(0).getProjectId(), 5);
        assertEquals("User value mismatch", (int) list.get(0).getUserId(), 1);
    }

}
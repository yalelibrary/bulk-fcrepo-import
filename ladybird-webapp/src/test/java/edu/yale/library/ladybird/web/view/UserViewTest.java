package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.beans.UserBuilder;
import edu.yale.library.ladybird.kernel.beans.User;

import edu.yale.library.ladybird.persistence.ServicesManager;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;


public class UserViewTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserViewTest.class);

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
    private UserDAO dao;

    @Test
    public void testSave() {
        final User user = build();
        try {
            dao.save(user);
            logger.debug("Saved item.");
            final List list = dao.findAll();
            assertEquals("Item count incorrect", list.size(), 1);
        } catch (Throwable e) {
            fail("Error testing saving or finding item");
        }
    }

    private User build() {
        User item = new UserBuilder().createUser();
        item.setUsername("test user");
        item.setPassword("test_pw");
        Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        item.setDateCreated(date);
        item.setDateEdited(date);
        item.setDateEdited(date);
        item.setDateLastused(date);
        return item;
    }


}
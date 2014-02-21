package view;

import edu.yale.library.ServicesManager;
import edu.yale.library.beans.UserBuilder;
import edu.yale.library.view.DaoInitializer;
import edu.yale.library.beans.User;
import edu.yale.library.dao.UserDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;


public class UserViewTest
{

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserViewTest.class);

    ServicesManager servicesManager;

    {
        DaoInitializer.injectFields(this);
    }

    @Before
    public void init()
    {
        servicesManager = new ServicesManager();
    }

    @After
    public void stopDB() throws SQLException
    {
      servicesManager.stopDB();
    }

    @Inject
    private UserDAO userDAO;

    @Test
    public void testSave()
    {
        servicesManager.initDB();
        User user = buildUser();
        try
        {
            userDAO.save(user);
            logger.debug("Saved item.");
            List list =  userDAO.findAll();
            assertEquals("Item count incorrect", list.size(), 1);
        }
        catch (Throwable e)
        {
            logger.error("Error testing saving or finding item", e);
        }
    }

    User buildUser()
    {
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
package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserExportField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserExportFieldTest extends AbstractPersistenceTest {

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
    private UserExportFieldDAO dao;

    @Test
    public void testSave() {
        final UserExportField item = new UserExportField(1, 11);
        List<UserExportField> list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final UserExportField userExportField = list.get(0);
        assertEquals("Value mismatch", (long) userExportField.getFdid(), 11);
    }

}
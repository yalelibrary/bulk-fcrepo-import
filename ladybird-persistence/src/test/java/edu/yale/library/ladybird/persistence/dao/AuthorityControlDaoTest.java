package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AuthorityControlDaoTest extends AbstractPersistenceTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPersistenceTest.class);

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
    private AuthorityControlDAO dao;

    @Test
    public void testSave() {
        final AuthorityControl item = new AuthorityControlBuilder().createAuthorityControl();
        item.setFdid(555);
        item.setValue("test value");
        item.setDate(new Date());
        item.setUserId(0);
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final AuthorityControl authorityControl = (AuthorityControl) list.get(0);
        assertEquals("Value mismatch", (long) authorityControl.getFdid(), 555);
        assertEquals("Value mismatch", authorityControl.getValue(), "test value");

    }

}

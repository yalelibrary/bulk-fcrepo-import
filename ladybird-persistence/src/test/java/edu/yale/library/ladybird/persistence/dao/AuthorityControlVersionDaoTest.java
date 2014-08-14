package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AuthorityControlVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AuthorityControlVersionDaoTest extends AbstractPersistenceTest {

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
    private AuthorityControlVersionDAO dao;

    @Test
    public void testSave() {
        final AuthorityControlVersion item = new AuthorityControlVersion();
        item.setFdid(555);
        item.setCode("unk");
        item.setValue("test value");
        item.setDate(new Date());
        item.setChangeDate(new Date());
        item.setAcid(22);
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
        final AuthorityControlVersion authorityControlVersion = (AuthorityControlVersion) list.get(0);
        assertEquals("Value mismatch", (long) authorityControlVersion.getFdid(), 555);
        assertEquals("Value mismatch", authorityControlVersion.getValue(), "test value");
    }

}

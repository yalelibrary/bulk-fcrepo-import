package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserProjectFieldExportOptionsTest extends AbstractPersistenceTest {

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
    private UserProjectFieldExportOptionsDAO dao;

    @Test
    public void testSave() {
        List<UserProjectFieldExportOptions> list = new ArrayList<>();
        final UserProjectFieldExportOptions item = new UserProjectFieldExportOptions();
        UserProjectFieldExportOptions item2 = new UserProjectFieldExportOptions();
        UserProjectFieldExportOptions item3 = new UserProjectFieldExportOptions();
        item.setUserId(1);
        item.setProjectId(1);
        item.setExport('n');
        item.setFdid(80);
        try {
            dao.save(item);
            list = dao.findAll();
            item2 = dao.findByUserAndProjectAndFdid(1, 1, 80);
            item3 = dao.findByUserAndProjectAndFdid(2, 2, 80);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Project value mismatch", (int) list.get(0).getProjectId(), 1);
        assertEquals("User value mismatch", (int) list.get(0).getUserId(), 1);
        assertEquals("Value mismatch", (long) list.get(0).getFdid(), 80);
        assertEquals("Value mismatch", (char) list.get(0).getExport(), 'n');
        assert (item2.getUserId() == 1);
        assert (item2.getFdid() == 80);

        assert (item3 == null);
    }

}
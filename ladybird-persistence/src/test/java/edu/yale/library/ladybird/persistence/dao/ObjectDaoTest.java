package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Before
    public void init() {
        initDB();
    }

    @After
    public void stop() throws SQLException {
        //TODO clean up
        List<Object> delList = dao.findAll();
        dao.delete(delList);
        assert (dao.findAll().size() == 0);
    }

    @Inject
    private ObjectDAO dao;

    @Test
    public void testSave() {
        final Object item = build();
        item.setRoid(1);
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Object o = (Object) list.get(0);
        assertEquals("Value mismatch", o.getProjectId(), 1);
        assertEquals("Value mismatch", o.getRoid(), 1);
    }

    @Test
    public void shouldFindparent() {
        final List<Object> itemList = buildComplexObjects();

        List list = null;
        try {
            dao.saveOrUpdateList(itemList);
            list = dao.findByParent(1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Object o = (Object) list.get(0);
        assertEquals("Value mismatch", o.getProjectId(), 1);
        assertEquals("Value mismatch", (long) o.getP_oid(), 1);
        assertEquals("Value mismatch", o.isChild(), true);

        assert (dao.childCount(1) == 1);
    }

    private Object build() {
        final Object item = new ObjectBuilder().setProjectId(1).createObject();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }

    private List<Object> buildComplexObjects() {

        final List<Object> objects = new ArrayList<>();

        final Object parent = new ObjectBuilder().setProjectId(1).setParent(true).setP_oid(0).createObject();
        final Date date = new Date();
        parent.setDate(date);

        objects.add(parent);

        final Object child = new ObjectBuilder().setProjectId(1).setParent(false).setP_oid(1).createObject();
        child.setDate(date);

        objects.add(child);

        return objects;
    }
}
package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.FieldMarcMapping;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class FieldMarcMappingDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Inject
    private FieldMarcMappingDAO dao;

    @Before
    public void init() {
        initDB();
    }

    @Test
    public void shouldSave() {
        final FieldMarcMapping item = new FieldMarcMapping();
        item.setFdid(70);
        item.setK1("245");
        item.setDate(new Date());
        List list = null;
        FieldMarcMapping dbItemByFdid = null;

        try {
            dao.save(item);
            list = dao.findAll();
            dbItemByFdid = dao.findByFdid(70);

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final FieldMarcMapping dbItem = (FieldMarcMapping) list.get(0);
        assertEquals("Value mismatch", (long) dbItem.getFdid(), 70);
        assertEquals("Value mismatch", dbItemByFdid.getK1(), "245");
    }

}

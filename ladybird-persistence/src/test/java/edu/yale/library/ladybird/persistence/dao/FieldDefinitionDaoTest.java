package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.FieldDefinition;
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
public class FieldDefinitionDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Inject
    private FieldDefinitionDAO dao;

    @Before
    public void init() {
        initDB();
    }

    @Test
    public void testSave() {
        final FieldDefinition item = new FieldDefinition();
        item.setFdid(555);
        item.setAcid(777);
        item.setDate(new Date());
        item.setLocked(true);
        item.setRequired(false);
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final FieldDefinition dbItem = (FieldDefinition) list.get(0);
        assertEquals("Value mismatch", (long) dbItem.getFdid(), 555);
        assertEquals("Value mismatch", dbItem.getAcid(), 777);
        assertEquals("Value mismatch", dbItem.isLocked(), true);
        assertEquals("Value mismatch", dbItem.isRequired(), false);
    }

}

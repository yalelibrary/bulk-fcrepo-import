package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
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


public class ObjectFileDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    private static final int PROJECT_ID_1 = 1;
    private static final int PROJECT_ID_2 = 2;

    @Before
    public void init() {
        initDB();
    }

    @After
    public void stop() throws SQLException {
        //TODO
    }

    @Inject
    private ObjectDAO objectDAO;

    @Inject
    private ObjectFileDAO dao;

    @Test
    public void shouldSave() {
        final List<ObjectFile> item = buildObjects();
        ObjectFile itemByOid = null;
        List<ObjectFile> list = null;
        List<ObjectFile> itemsByProject1 = null;
        List<ObjectFile> itemsByProject2 = null;
        try {
            dao.saveOrUpdateList(item);
            list = dao.findAll();
            itemByOid = dao.findByOid(item.get(0).getOid());
            itemsByProject1 = dao.findByProject(PROJECT_ID_1);
            itemsByProject2 = dao.findByProject(PROJECT_ID_2);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 2);
        assertEquals("Value mismatch", list.get(0).getFileName(), "tmpFile");
        assertEquals(itemByOid.getFileName(), "tmpFile");

        assert (itemsByProject1.size() == 1);
        assert (itemsByProject1.get(0).getFileName().equals("tmpFile"));

        assert (itemsByProject2.size() == 1);
        assert (itemsByProject2.get(0).getFileName().equals("project 2 file name"));
    }

    private List<ObjectFile> buildObjects() {

        assert (objectDAO.findAll().size() == 0);

        //save object 1 for dao.findByProject()
        final Object object = new ObjectBuilder().setDate(new Date()).setProjectId(PROJECT_ID_1).createObject();
        objectDAO.save(object);

        //save object 2 for dao.findByProject()
        final Object object2 = new ObjectBuilder().setDate(new Date()).setProjectId(PROJECT_ID_2).createObject();
        objectDAO.save(object2);


        final ObjectFile objectFile1 = new ObjectFileBuilder().setOid(object.getOid()).setFileName("tmpFile").createObjectFile();
        final Date date = new Date();
        objectFile1.setDate(date);

        final ObjectFile objectFile2 = new ObjectFileBuilder().setOid(object2.getOid()).setFileName("project 2 file name").createObjectFile();
        final Date date2 = new Date();
        objectFile2.setDate(date2);

        List<ObjectFile> objects = new ArrayList<>();
        objects.add(objectFile1);
        objects.add(objectFile2);

        return objects;
    }
}
package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.ProjectBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProjectDaoTest extends AbstractPersistenceTest {

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
    private ProjectDAO dao;

    @Test
    public void testSave() {
        final Project item = build();
        List list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Project project = (Project) list.get(0);
        assertEquals("Value mismatch", project.getLabel(), "Project X");
    }

    private Project build() {
        final Project item = new ProjectBuilder().setLabel("Project X").createProject();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
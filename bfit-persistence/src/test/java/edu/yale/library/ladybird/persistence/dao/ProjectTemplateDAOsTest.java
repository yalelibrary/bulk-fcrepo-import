package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateAcid;
import edu.yale.library.ladybird.entity.ProjectTemplateAcidBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.entity.ProjectTemplateStringsBuilder;
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

public class ProjectTemplateDAOsTest extends AbstractPersistenceTest {

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
    private ProjectTemplateDAO dao;

    @Inject
    private ProjectTemplateAcidDAO acidDAO;

    @Inject
    private ProjectTemplateStringsDAO projectTemplateStringsDAO;

    @Test
    public void shouldSaveProjectTemplate() {
        final ProjectTemplate item = new ProjectTemplateBuilder().setProjectId(1).setCreator(1).setLabel("Project X").setDate(new Date()).createProjectTemplate();
        List list = null;
        ProjectTemplate toFind = null;
        int projectCount = 0;
        List<ProjectTemplate> projectTemplateList = new ArrayList<>(); //for a particular project id
        try {
            dao.save(item);
            list = dao.findAll();

            projectCount = dao.getCountByLabel("Project X");

            projectTemplateList = dao.findByProjectId(1);
            toFind = dao.findByLabel("Project X");
        } catch (Throwable e) {
            fail("Error testing saving or finding item" + e.getMessage());
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ProjectTemplate project = (ProjectTemplate) list.get(0);
        assertEquals("Value mismatch", project.getLabel(), "Project X");
        assert (projectCount == 1);

        assert (projectTemplateList.size() == 1);

        assert (toFind.getProjectId() == 1);
        assert (toFind.getCreator() == 1);
    }

    @Test
    public void shouldSaveProjectTemplateACID() {
        final ProjectTemplateAcid item = new ProjectTemplateAcidBuilder().setAcid(111).setFdid(69).createProjectTemplateAcid();
        List list = null;
        try {
            acidDAO.save(item);
            list = acidDAO.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ProjectTemplateAcid project = (ProjectTemplateAcid) list.get(0);
        assertEquals("Value mismatch", (long) project.getAcid(), 111);
    }

    @Test
    public void shouldSaveProjectTemplateStrings() {
        final ProjectTemplateStrings item = new ProjectTemplateStringsBuilder().setFdid(69).setValue("Locomotive").setTemplateId(1).createProjectTemplateStrings();
        List list = null;
        try {
            projectTemplateStringsDAO.save(item);
            list = projectTemplateStringsDAO.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final ProjectTemplateStrings val = (ProjectTemplateStrings) list.get(0);
        assertEquals("Value mismatch", val.getValue(), "Locomotive");
    }

}
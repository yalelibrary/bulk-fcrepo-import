package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.entity.UserProjectBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserProjectDaoTest extends AbstractPersistenceTest {

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
    private UserProjectDAO dao;

    @Test
    public void testSave() {
        List projectAList = null;
        List projectBList = null;

        final String defaultProjectRole = "reader";

        //Group1: 2 users, prjoect A
        final int defaultProjectId = 0;
        UserProject userProj1 = null;
        UserProject userProj2 = null;

        //Group2: 1 user, project B
        final int otherProjectId = 1;
        UserProject other = null;

        try {
            userProj1 = buildUserProject(0, defaultProjectId, defaultProjectRole);
            dao.save(userProj1);

            userProj2 = buildUserProject(1, defaultProjectId, defaultProjectRole);
            dao.save(userProj2);

            other = buildUserProject(2, otherProjectId, defaultProjectRole);
            dao.save(other);

            projectAList = dao.findByProjectId(defaultProjectId);

            projectBList = dao.findByProjectId(otherProjectId);

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", projectAList.size(), 2);

        final UserProject userProjDbEntry1 = (UserProject) projectAList.get(0);

        assertEquals("Value mismatch", userProjDbEntry1.getProjectId(), userProj1.getProjectId());
        assertEquals("Value mismatch", userProjDbEntry1.getUserId(), userProj1.getUserId());

        final UserProject userProjectDbEntry2 = (UserProject) projectAList.get(1);
        assertEquals("Value mismatch", userProjectDbEntry2.getProjectId(), userProj2.getProjectId());
        assertEquals("Value mismatch", userProjectDbEntry2.getUserId(), userProj2.getUserId());

        assertEquals("Item count incorrect", projectBList.size(), 1);
        final UserProject otherDbEntry2 = (UserProject) projectBList.get(0);
        assertEquals("Value mismatch", otherDbEntry2.getProjectId(), other.getProjectId());
        assertEquals("Value mismatch", otherDbEntry2.getUserId(), other.getUserId());
    }

    private UserProject buildUserProject(final int userId, final int projectId, final String role) {
        final UserProject item = new UserProjectBuilder().setUserId(userId).setProjectId(projectId)
                .setRole(role).createUserProject();
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        return item;
    }
}
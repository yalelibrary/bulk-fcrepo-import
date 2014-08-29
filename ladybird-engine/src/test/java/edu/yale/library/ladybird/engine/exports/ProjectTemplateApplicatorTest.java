package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.ObjectTestsHelper;
import edu.yale.library.ladybird.engine.metadata.ProjectTemplateApplicator;
import edu.yale.library.ladybird.engine.metadata.Rollbacker;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.entity.ProjectTemplateStringsBuilder;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateStringsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ProjectTemplateHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ProjectTemplateStringsHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

public class ProjectTemplateApplicatorTest extends AbstractDBTest {

    private Logger logger = getLogger(ProjectTemplateApplicatorTest.class);

    private static final int ACID_FDID = 59;
    private static final int STRING_FDID = 70;

    //TODO
    private final ObjectDAO objectDAO = new ObjectHibernateDAO();
    private final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();
    private final FieldDefinitionDAO fieldDAO = new FieldDefinitionHibernateDAO();

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }

    //TODO save an acid which is mvf
    @Test
    public void shouldApplyTemplate() {
        final int userId = 1;
        final int templateId = 0;
        try {
            //1. save sample object with 2 fields (for object_acid and object_string)

            saveTestObject();

            //2. apply template
            ProjectTemplateApplicator projectTemplateApplicator = new ProjectTemplateApplicator();
            ProjectTemplate projectTemplate = new ProjectTemplateBuilder().setProjectId(1).setCreator(1)
                    .setDate(new Date()).setLabel("Vrc").createProjectTemplate();
            new ProjectTemplateHibernateDAO().save(projectTemplate);

            ProjectTemplateStringsDAO templateStringDAO = new ProjectTemplateStringsHibernateDAO();

            ProjectTemplateStrings templateString1 = new ProjectTemplateStringsBuilder().setFdid(STRING_FDID)
                    .setValue("--S").setTemplateId(templateId).createProjectTemplateStrings();
            templateStringDAO.save(templateString1);

            ProjectTemplateStrings templateString2 = new ProjectTemplateStringsBuilder().setFdid(ACID_FDID)
                    .setValue("--A").setTemplateId(templateId).createProjectTemplateStrings();
            templateStringDAO.save(templateString2);

            assert (templateStringDAO.findAll().size() == 2); //one for acid, one for string

            projectTemplateApplicator.applyTemplate(projectTemplate, userId);

            //3. confirm template application:
            ObjectString objectString = objectStringDAO.findAll().get(0);
            assertEquals(objectString.getValue(), "test--S");

            List<ObjectAcid> objectAcidList = objectAcidDAO.findAll(); //should've saved 2 acids

            //need this as init db only has one acid fdid whose multivalued is set to false.
            final boolean isMultiValued = fieldDAO.findByFdid(ACID_FDID).isMultivalue();

            if (isMultiValued) {
                assert (objectAcidList.size() == 2);
                assertEquals(objectAcidList.get(0).getValue(), 1);
                assertEquals(objectAcidList.get(1).getValue(), 2);
            } else {
                assert (objectAcidList.size() == 1);
                assertEquals(objectAcidList.get(0).getValue(), 2);
            }


            final List<ObjectAcid> objAcidList2 = objectAcidDAO.findListByOidAndFdid(1, ACID_FDID);

            if (isMultiValued) {
                assert (objAcidList2.size() == 2);
                assert (objectAcidDAO.findAll().size() == 2);

                AuthorityControl ex = authorityControlDAO.findByAcid(objectAcidList.get(0).getValue());
                assertEquals(ex.getValue(), "acid value");

                AuthorityControl ac = authorityControlDAO.findByAcid(objectAcidList.get(1).getValue());
                assertEquals(ac.getValue(), "--A");
            } else {
                AuthorityControl ex = authorityControlDAO.findByAcid(objectAcidList.get(0).getValue());
                assertEquals(ex.getValue(), "--A");
            }

            //4. TODO check versions created
        } catch (Exception e) {
            logger.error("Error", e);
            fail("Error updating" + e.getMessage());
        }
    }

    //TODO remove duplicate code
    @Test
    public void shouldRollbackAppliedTemplate() {
        final int testUserId = 1;
        final int templateId = 0;
        try {
            //1. save sample object with 2 fields (for object_acid and object_string)
            //saveFdids();
            //we assume fdid 70 is string and fdid 71 is an acid
            assert (new FieldDefinitionHibernateDAO().findAll().size() == 2);

            int oid = saveTestObject();

            //2. apply template
            ProjectTemplateApplicator projectTemplateApplicator = new ProjectTemplateApplicator();
            ProjectTemplate projectTemplate = new ProjectTemplateBuilder().setProjectId(1).setCreator(1)
                    .setDate(new Date()).createProjectTemplate();
            new ProjectTemplateHibernateDAO().save(projectTemplate);

            ProjectTemplateStringsDAO projectTemplateStringsDAO = new ProjectTemplateStringsHibernateDAO();

            ProjectTemplateStrings projectTemplateStrings = new ProjectTemplateStringsBuilder().setFdid(STRING_FDID)
                    .setValue("--S").setTemplateId(templateId).createProjectTemplateStrings();
            projectTemplateStringsDAO.save(projectTemplateStrings);

            ProjectTemplateStrings projectTemplateStrings2 = new ProjectTemplateStringsBuilder().setFdid(ACID_FDID)
                    .setValue("--A").setTemplateId(templateId).createProjectTemplateStrings();
            projectTemplateStringsDAO.save(projectTemplateStrings2);

            assert (projectTemplateStringsDAO.count() == 2); //one for acid, one for string

            projectTemplateApplicator.applyTemplate(projectTemplate, testUserId);

            //3. confirm template application:
            ObjectString objectString = objectStringDAO.findAll().get(0);
            assertEquals(objectString.getValue(), "test--S");

            List<ObjectAcid> objectAcidList = objectAcidDAO.findAll(); //should've saved 2 acids

            final boolean isMultiValued = fieldDAO.findByFdid(ACID_FDID).isMultivalue();

            if (isMultiValued) {
                assertEquals(objectAcidList.get(0).getValue(), 1);
                assertEquals(objectAcidList.get(1).getValue(), 2);

                AuthorityControl ex = authorityControlDAO.findByAcid(objectAcidList.get(0).getValue());
                assertEquals(ex.getValue(), "acid value");

                AuthorityControl ac = authorityControlDAO.findByAcid(objectAcidList.get(1).getValue());
                assertEquals(ac.getValue(), "--A");
            } else {
                assertEquals(objectAcidList.get(0).getValue(), 2);

                AuthorityControl ex = authorityControlDAO.findByAcid(objectAcidList.get(0).getValue());
                assertEquals(ex.getValue(), "--A");
            }

            List<ObjectAcid> objAcidList2 = objectAcidDAO.findListByOidAndFdid(1, ACID_FDID);

            if (isMultiValued) {
                assert (objAcidList2.size() == 2);
            } else {
                assert (objAcidList2.size() == 1);
            }

            //4. rollback and verify that original value exists:
            int version = ObjectTestsHelper.getMaxVersion(oid);

            Rollbacker rollbacker = new Rollbacker();
            rollbacker.rollback(oid, version, 0);

            assert (ObjectTestsHelper.getMaxVersion(oid) == 2);

            List<ObjectString> objectStrings = objectStringDAO.findByOid(oid);

            assertEquals(objectStrings.get(0).getValue(), "test");
            assert (objectStrings.size() == 1);

            //TODO same for acid. . .
        } catch (Exception e) {
            logger.error("Error", e);
            fail("Error verifiying/testing rollback of template.");
        }
    }

    private int saveTestObject() {
        try {
            final Date d = new Date();
            final Object object = new ObjectBuilder().setOid(1).setProjectId(1).setUserId(1).setDate(d).createObject();
            final int oid = objectDAO.save(object);

            assert (objectDAO.count() == 1);

            ObjectString objectString = new ObjectStringBuilder().setDate(d).setFdid(STRING_FDID).setOid(oid)
                    .setValue("test").createObjectString();
            objectStringDAO.save(objectString);
            assert (objectStringDAO.findAll().size() == 1);

            AuthorityControl authorityControl = new AuthorityControlBuilder().setValue("acid value").setFdid(ACID_FDID).setDate(d).createAuthorityControl();
            int acid = authorityControlDAO.save(authorityControl);

            ObjectAcid objectStringForAcid = new ObjectAcidBuilder().setObjectId(oid).setFdid(ACID_FDID)
                    .setValue(acid).setDate(d).createObjectAcid();

            objectAcidDAO.save(objectStringForAcid);

            assert (objectStringDAO.findAll().size() == 1);
            assert (objectAcidDAO.findAll().size() == 1);
            assert (authorityControlDAO.findAll().size() == 1);

            return oid;
        } catch (Exception e) {
            throw e;
        }
    }

}

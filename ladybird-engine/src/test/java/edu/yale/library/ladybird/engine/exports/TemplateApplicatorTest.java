package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.metadata.TemplateApplicator;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldDefinitionBuilder;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.util.Date;

public class TemplateApplicatorTest extends AbstractDBTest {

    private Logger logger = getLogger(TemplateApplicatorTest.class);

    private static final int ACID_FDID = 59;

    //TODO
    private final ObjectDAO objectDAO = new ObjectHibernateDAO();
    private final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private final FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();
    private final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }

    @Test
    public void shouldUpdateMetadata() {
        try {
            //1. save sample object with 2 fields (for object_acid and object_string)
            saveFdids();
            //we assume fdid 70 is string and fdid 71 is an acid
            assert (new FieldDefinitionHibernateDAO().findAll().size() == 2);

            saveTestObject();

            //2. apply template
            TemplateApplicator templateApplicator = new TemplateApplicator();
            ProjectTemplate projectTemplate = new ProjectTemplateBuilder().setProjectId(1).setCreator(1)
                    .setDate(new Date()).setLabel("Vrc").createProjectTemplate();
            new ProjectTemplateHibernateDAO().save(projectTemplate);

            ProjectTemplateStringsDAO projectTemplateStringsDAO = new ProjectTemplateStringsHibernateDAO();

            ProjectTemplateStrings projectTemplateStrings = new ProjectTemplateStrings();
            projectTemplateStrings.setFdid(70);
            projectTemplateStrings.setValue("--S");
            projectTemplateStrings.setTemplateId(0);
            projectTemplateStringsDAO.save(projectTemplateStrings);

            ProjectTemplateStrings projectTemplateStrings2 = new ProjectTemplateStrings();
            projectTemplateStrings2.setFdid(ACID_FDID); //ACID
            projectTemplateStrings2.setValue("--A");
            projectTemplateStrings2.setTemplateId(0);
            projectTemplateStringsDAO.save(projectTemplateStrings2);

            assert (projectTemplateStringsDAO.findAll().size() == 2); //one for acid, one for string

            templateApplicator.updateObjectMetadata(projectTemplate);

            //3. confirm template application:
            ObjectString objectString = objectStringDAO.findAll().get(0);
            assertEquals(objectString.getValue(), "test--S");

            ObjectAcid objectAcid = objectAcidDAO.findAll().get(0);
            assertEquals(objectAcid.getValue(), 1);

            AuthorityControl ac = authorityControlDAO.findByAcid(1);
            assertEquals(ac.getValue(), "acid value--A");
        } catch (Exception e) {
            logger.error("Error", e);
            fail("Error updating" + e.getMessage());
        }
    }

    private void saveTestObject() {
        try {
            final Date d = new Date();
            Object object = new ObjectBuilder().setOid(1).setProjectId(1).setUserId(1).setDate(d).createObject();
            objectDAO.save(object);

            assert (objectDAO.findAll().size() == 1);

            ObjectString objectString = new ObjectStringBuilder().setDate(d).setFdid(70).setOid(1)
                    .setValue("test").createObjectString();
            objectStringDAO.save(objectString);
            assert (objectStringDAO.findAll().size() == 1);

            AuthorityControl authorityControl = new AuthorityControlBuilder().createAuthorityControl();

            authorityControl.setValue("acid value");
            authorityControl.setFdid(ACID_FDID);
            authorityControl.setDate(d);
            int acid = authorityControlDAO.save(authorityControl);

            ObjectAcid objectStringForAcid = new ObjectAcidBuilder().createObjectAcid();
            objectStringForAcid.setObjectId(1);
            objectStringForAcid.setFdid(ACID_FDID);
            objectStringForAcid.setValue(acid);
            objectStringForAcid.setDate(d);

            objectAcidDAO.save(objectStringForAcid);

            assert (objectStringDAO.findAll().size() == 1);
            assert (objectAcidDAO.findAll().size() == 1);
            assert (authorityControlDAO.findAll().size() == 1);
        } catch (Exception e) {
            throw e;
        }
    }

    private void saveFdids() {
        Date d = new Date();
        try {
            FieldDefinition fdid = new FieldDefinitionBuilder().setFdid(70).setDate(d).createFieldDefinition();
            fieldDefinitionDAO.save(fdid);

            FieldDefinition fdid2 = new FieldDefinitionBuilder().setFdid(ACID_FDID).setDate(d).createFieldDefinition();
            fieldDefinitionDAO.save(fdid2);
        } catch (Exception e) {
            throw e;
        }
    }

}

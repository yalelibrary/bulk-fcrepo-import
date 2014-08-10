package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.ObjectTestsHelper;
import edu.yale.library.ladybird.engine.metadata.Rollbacker;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class RollbackTest extends AbstractDBTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void shouldRollback() {
        final int oid = 1, version = 0, userId = 0, fdid = 70, acidFdid = 71;
        String oldAcidValue = "Old Acid Value";
        persistDummyObject(oid, fdid, "Old Value", acidFdid, oldAcidValue);
        editDummyObject(oid);

        try {
            new Rollbacker().rollback(oid, version, userId);
        } catch (Exception e) {
            logger.error("Error", e);
        }
        assertEquals(ObjectTestsHelper.fdidValue(oid, fdid), ("New Value"));
        assertEquals(ObjectTestsHelper.fdidAcidValue(oid, acidFdid), "New Acid Value");
    }

    private void persistDummyObject(int oid, int fdid, String value, int acidFdid, String acidValue) {
        Date date = new Date();
        Object object = new ObjectBuilder().setDate(date).setProjectId(0).setUserId(0).createObject();
        ObjectDAO objectDAO = new ObjectHibernateDAO();
        objectDAO.save(object);

        ObjectTestsHelper.writeDummyObjString(oid, fdid, value);
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, acidValue);
    }

    private void editDummyObject(int oid) {
        ObjectStringDAO dao = new ObjectStringHibernateDAO();
        ObjectString objectString = dao.findByOidAndFdid(oid, 70);
        objectString.setValue("New Value");
        dao.updateItem(objectString);

        //save new stupid acid
        int acid = ObjectTestsHelper.writeDummyAcid(71, "New Acid Value");

        ObjectAcid objectAcid = new ObjectAcidHibernateDAO().findByOidAndFdid(oid, 71);
        objectAcid.setValue(acid);
        new ObjectAcidHibernateDAO().updateItem(objectAcid); //notice updated. a new entry is created.
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }
}

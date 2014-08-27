package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.ObjectTestsHelper;
import edu.yale.library.ladybird.engine.metadata.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.metadata.MetadataEditor;
import edu.yale.library.ladybird.engine.metadata.Rollbacker;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectVersionHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RollbackTest extends AbstractDBTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final int fdid = 70, acidFdid = 59;

    //final int acidFdid = 59;
    //final int stringFdid = 70;

    @Test
    public void shouldRollback() {
        final int oid = 1, version = 1, userId = 0;
        final String oldStrValue = "Old Value";
        final String oldAcidValue = "Old Acid Value";

        //1. persist dummy object and verify that old value is written
        persistDummyObject(oid, fdid, oldStrValue, acidFdid, oldAcidValue);
        List<ObjectString> osList = ObjectTestsHelper.fdidValue(oid, fdid);
        assert (osList.get(0).getValue().equals(oldStrValue)); //TODO same for acid

        //2. edit dummy object and verify new value is written
        editDummyObject(oid, userId);
        List<ObjectString> osList1 = ObjectTestsHelper.fdidValue(oid, fdid);
        assert (osList1.get(0).getValue().equals("New Value"));

        //3. Restoring the value
        try {
            new Rollbacker().rollback(oid, version, userId);
        } catch (Exception e) {
            logger.error("Error", e);
        }

        //4. Verify object restoration:
        List<ObjectString> osList3 = ObjectTestsHelper.fdidValue(oid, fdid);
        assertEquals(osList3.get(0).getValue(), oldStrValue);
        assertEquals(ObjectTestsHelper.fdidAcidValue(oid, acidFdid), oldAcidValue);

        //5. (optional) at this point two versions for objects should exist == one for edit and one for rollback
        assert (new ObjectVersionHibernateDAO().findByOid(oid).size() == 2);
    }

    @Ignore("TODO")
    @Test
    public void shouldRollbackToMultiple() {
        final int oid = 1, version = 1, userId = 0;
        final String[] oldStrValue = {"Old Value 1", "Old Value 2"};
        final String[] oldAcidValue = {"Old Acid Value 1", "Old Acid Value 2"};

        //1. persist dummy object and verify that old value is written
        persistDummyObjectMultipleFields(oid, fdid, oldStrValue, acidFdid, oldAcidValue);

        List<ObjectString> osList = ObjectTestsHelper.fdidValue(oid, fdid);
        assert (osList.get(0).getValue().equals(oldStrValue[0])); //TODO same for acid
        assert (osList.get(1).getValue().equals(oldStrValue[1]));

        //2. edit dummy object and verify new value is written
        editDummyObject(oid, userId); //FIXME

        List<ObjectString> osList1 = ObjectTestsHelper.fdidValue(oid, fdid);

        logger.debug("os list={}", osList1);

        assert (osList1.get(0).getValue().equals("New Value"));

        //3. Restore the value
        try {
            new Rollbacker().rollback(oid, version, userId);
        } catch (Exception e) {
            logger.error("Error", e);
        }

        //4. Verify object restoration:
        List<ObjectString> osList3 = ObjectTestsHelper.fdidValue(oid, fdid);
        assertEquals(osList3.get(0).getValue(), oldStrValue);
        assertEquals(ObjectTestsHelper.fdidAcidValue(oid, acidFdid), oldAcidValue);

        //5. (optional) at this point two versions for objects should exist == one for edit and one for rollback
        assert (new ObjectVersionHibernateDAO().findByOid(oid).size() == 2);
    }

    private void persistDummyObject(int oid, int fdid, String stringValue, int acidFdid, String acidValue) {
        //1. persist field definitons:
        FieldDefinition fieldDefintion = new FieldDefinition(fdid);
        FieldDefinition fieldDefinition2 = new FieldDefinition(acidFdid);
        //new FieldDefinitionHibernateDAO().saveOrUpdateList(Arrays.asList(fieldDefintion, fieldDefinition2));

        //assert (new FieldDefinitionHibernateDAO().findAll().size() == 2);

        Date date = new Date();
        Object object = new ObjectBuilder().setDate(date).setProjectId(0).setUserId(0).createObject();
        ObjectDAO objectDAO = new ObjectHibernateDAO();
        objectDAO.save(object);

        ObjectTestsHelper.writeDummyObjString(oid, fdid, stringValue);
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, acidValue);
    }

    private void persistDummyObjectMultipleFields(int oid, int fdid, String[] stringValue, int acidFdid, String[] acidValue) {
        //1. persist field definitons:
        FieldDefinition fieldDefintion = new FieldDefinition(fdid);
        FieldDefinition fieldDefinition2 = new FieldDefinition(acidFdid);
        new FieldDefinitionHibernateDAO().saveOrUpdateList(Arrays.asList(fieldDefintion, fieldDefinition2));

        assert (new FieldDefinitionHibernateDAO().findAll().size() == 2);

        Date date = new Date();
        Object object = new ObjectBuilder().setDate(date).setProjectId(0).setUserId(0).createObject();
        ObjectDAO objectDAO = new ObjectHibernateDAO();
        objectDAO.save(object);

        ObjectTestsHelper.writeDummyObjString(oid, fdid, stringValue[0]);
        ObjectTestsHelper.writeDummyObjString(oid, fdid, stringValue[1]);

        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, acidValue[0]);
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, acidValue[1]);
    }

    //This should update with versioning
    private void editDummyObject(int oid, int userId) {
        MetadataEditor metadataEditor = new MetadataEditor();
        metadataEditor.updateOidMetadata(oid, userId, getUpdateFdidValueList());
    }

    //The update to be applied
    private List<FieldDefinitionValue> getUpdateFdidValueList() {
        List<FieldDefinitionValue> fdidList = new ArrayList<>();
        fdidList.add(getFdidValue(fdid, Arrays.asList("New Value")));
        fdidList.add(getFdidValue(acidFdid, Arrays.asList("New Acid Value")));
        return fdidList;
    }

    private FieldDefinitionValue getFdidValue(int fdid, List<String> value) {
        FieldDefinition fieldDefinition = new FieldDefinition(fdid);
        return  new FieldDefinitionValue(fieldDefinition, value);
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

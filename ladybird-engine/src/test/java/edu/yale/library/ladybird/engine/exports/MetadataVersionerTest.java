package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.ObjectTestsHelper;
import edu.yale.library.ladybird.engine.metadata.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.metadata.MetadataVersioner;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.yale.library.ladybird.entity.Object;

public class MetadataVersionerTest extends AbstractDBTest {

    @Test
    public void shouldUpdateMetadata() {
        MetadataVersioner metadataVersioner = new MetadataVersioner();
        int testOid = 1;
        int testUserId = 1;
        List<FieldDefinitionValue> fieldDefinitionValueList = getFdidValueList();

        writeDummyObject(); // write object values

        assert (ObjectTestsHelper.fdidValue(1, 71).equals("String Value"));
        assert (ObjectTestsHelper.fdidAcidValue(1, 69).equals("String Acid value"));

        metadataVersioner.updateOidMetadata(testOid, testUserId, fieldDefinitionValueList);

        //read back metadata:
        assert (ObjectTestsHelper.fdidValue(1, 71).equals("String value 1"));

        //to make sure the value is pointed to, and not a duplicate entry getting created by ObjectString
        assert (new ObjectAcidHibernateDAO().findAll().size() == 1);

        assert (ObjectTestsHelper.fdidAcidValue(1, 69).equals("Acid value 1"));
    }

    private void writeDummyObject() {
        Date d = new Date();
        Object object = new ObjectBuilder().setUserId(0).setProjectId(0).setDate(d).createObject();
        new ObjectHibernateDAO().save(object);

        assert (new ObjectHibernateDAO().findAll().size() == 1);

        ObjectTestsHelper.writeDummyObjAcid(1, 69, "String Acid value");
        ObjectTestsHelper.writeDummyObjString(1, 71, "String Value");
    }

    private List<FieldDefinitionValue> getFdidValueList() {
        List<FieldDefinitionValue> fdidList = new ArrayList();
        fdidList.add(getFdidValue(69, "Acid value 1"));
        fdidList.add(getFdidValue(71, "String value 1"));
        return fdidList;
    }

    private FieldDefinitionValue getFdidValue(int fdid, String value) {
        FieldDefinition fieldDefinition = new FieldDefinition(fdid);
        FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue(fieldDefinition, value);
        return fieldDefinitionValue;
    }

    @Test
    public void versionStrings() {
        ObjectStringVersionDAO objectStringVersionDAO = new ObjectStringVersionHibernateDAO();
        int obStr = ObjectTestsHelper.writeDummyObjString(1, 70, "Old value");

        MetadataVersioner metadataVersioner = new MetadataVersioner();
        List<ObjectString> list = getEmptyList();
        list.add(new ObjectStringHibernateDAO().findAll().get(0));
        metadataVersioner.versionStrings(list);

        assert (objectStringVersionDAO.findAll().get(0).getVersionId() == 1);
    }

    @Test
    public void shouldVersionAcid() {
        ObjectAcidVersionDAO dao = new ObjectAcidVersionHibernateDAO();
        int objAcid = ObjectTestsHelper.writeDummyObjAcid(1, 70, "Old Acid Value");

        assert (dao.findAll().isEmpty());

        List<ObjectAcid> list = getEmptyList();
        list.add(new ObjectAcidHibernateDAO().findAll().get(0));

        MetadataVersioner metadataVersioner = new MetadataVersioner();
        metadataVersioner.versionAcid(list);

        //logger.debug("Object acid versions={}", dao.findAll().toString());
        assert (dao.findAll().get(0).getVersionId() == 1);
    }

    @Test
    public void shouldVersionObject() {
        MetadataVersioner metadataVersioner = new MetadataVersioner();
        metadataVersioner.versionObject(1, 1);
        assert (metadataVersioner.getLastObjectVersion(1) == 1);
    }

    @Test
    public void shouldGetLastVersion() {
        ObjectTestsHelper.writeDummyObjVersion(new int[]{1, 2});
        MetadataVersioner metadataVersioner = new MetadataVersioner();
        assert (metadataVersioner.getLastObjectVersion(1) == 2);
    }

    @Before
    public void init() {
        super.init();

    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }

    public List getEmptyList() {
        return new ArrayList();
    }
}

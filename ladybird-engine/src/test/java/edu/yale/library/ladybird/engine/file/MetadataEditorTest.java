package edu.yale.library.ladybird.engine.file;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.ObjectTestsHelper;
import edu.yale.library.ladybird.engine.metadata.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.metadata.MetadataEditor;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
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

import static junit.framework.Assert.assertEquals;

/**
 * Tests metadata editing. Multiple values are tested.
 */
public class MetadataEditorTest extends AbstractDBTest {

    private Logger logger = LoggerFactory.getLogger(MetadataEditorTest.class);

    final int acidFdid = 59;
    final int stringFdid = 70;



    /**
     * Tests handling of multiple fdid metadata.
     * Makes an edit and tests whether edit was applied and the metadata items were versioned.
     */
    @Ignore("until mocked")
    @Test
    public void shouldUpdateMultipleMetadata() {
        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectStringDAO osDAO = new ObjectStringHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();

        assert (oaDAO.count() == 0);
        assert (osDAO.count() == 0);

        MetadataEditor metadataEditor = new MetadataEditor();
        //int testOid = 1;
        int testUserId = 1;
        List<FieldDefinitionValue> fieldDefinitionValueList = getFdidValueList();
        assert (fieldDefinitionValueList.size() == 2);

        int oid = writeDummyObject(); // write object values
        logger.debug("Created oid={}", oid);

        //Let us write 2 strings and 2 acids for 2 fdids:
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, "String Acid value");
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, "String Acid value 2");
        ObjectTestsHelper.writeDummyObjString(oid, stringFdid, "String Value");
        ObjectTestsHelper.writeDummyObjString(oid, stringFdid, "Another String Value");

        assert (ObjectTestsHelper.fdidValue(oid, stringFdid).size() == 2);
        assert (ObjectTestsHelper.fdidAcidValueList(oid, acidFdid).size() == 2);

        metadataEditor.updateOidMetadata(oid, testUserId, fieldDefinitionValueList);

        //read back metadata:
        //1. make sure only the same number of fields exist

        assert (osDAO.findAll().size() == 2);
        assertEquals(oaDAO.findAll().size(), 2);

        //2. make sure object string edits were applied
        List<ObjectString> objectStrings = ObjectTestsHelper.fdidValue(oid, stringFdid);

        assertEquals(objectStrings.get(0).getValue().toLowerCase(), "new string value 1");
        assertEquals(objectStrings.get(1).getValue().toLowerCase(), "new string value 2");

        //3. make sure object acid edits were applied
        List<AuthorityControl> acList = ObjectTestsHelper.fdidAcidValueList(oid, acidFdid);

        assert (acList.get(0).getValue().equalsIgnoreCase("WHATEVER 1"));
        assert (acList.get(1).getValue().equalsIgnoreCase("Whatever 2"));

        //4. Test for versioning
        assert (new ObjectVersionHibernateDAO().findByOid(oid).size() == 1);

        ObjectStringVersionDAO osvDAO = new ObjectStringVersionHibernateDAO();
        List<ObjectStringVersion> osvList = osvDAO.findListByOidAndFdidAndVersion(oid, stringFdid, 1);
        assert (osvList.size() == 2);
        assert (osvList.get(0).getValue().equalsIgnoreCase("String value"));
        assert (osvList.get(1).getValue().equalsIgnoreCase("Another String value"));

        ObjectAcidVersionDAO oavDAO = new ObjectAcidVersionHibernateDAO();
        List<ObjectAcidVersion> oavList = oavDAO.findListByOidAndFdidAndVersion(oid, acidFdid, 1);
        assert (oavList.size() == 2);

        int acid1 = oavList.get(0).getValue();
        assert (authDAO.findByAcid(acid1).getValue().equalsIgnoreCase("String Acid Value"));

        int acid2 = oavList.get(1).getValue();
        assert (authDAO.findByAcid(acid2).getValue().equalsIgnoreCase("String Acid Value 2"));

        assert (authDAO.findAll().size() == 4);

        //clean up:
        authDAO.deleteAll();
        osvDAO.deleteAll();
        oavDAO.deleteAll();
        oaDAO.deleteAll();
        osDAO.deleteAll();
    }

    /**
     * Tests handling of multiple fdid metadata.
     * Makes an edit and tests whether edit was applied and the metadata items were versioned.
     */
    @Ignore("until mocked")
    @Test
    public void shouldUpdateMultipleDropdownMetadata() {

        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();

        MetadataEditor metadataEditor = new MetadataEditor();
        //int testOid = 1;
        int testUserId = 1;
        List<FieldDefinitionValue> fieldDefinitionValueList = getDropDownFdidValueList();
        assertEquals(fieldDefinitionValueList.size(), 1);

        //1. Create an acid that should be shared since it's a drop down:
        AuthorityControl sharedAcid = new AuthorityControlBuilder().setFdid(acidFdid).setDate(new Date()).setValue("WHATEVER 1").createAuthorityControl();
        authDAO.save(sharedAcid);
        assert (authDAO.findAll().size() == 1);

        //create object and acid values
        int oid = writeDummyObject(); // write object, not acid or string values
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, "String Acid value");
        ObjectTestsHelper.writeDummyObjAcid(oid, acidFdid, "String Acid value 2");

        assert (ObjectTestsHelper.fdidAcidValueList(oid, acidFdid).size() == 2);

        metadataEditor.updateOidMetadata(oid, testUserId, fieldDefinitionValueList);

        //read back metadata:
        //1. make sure only the same number of fields exist
        assert (oaDAO.findAll().size() == 2);

        //3. make sure object acid edits were applied
        List<AuthorityControl> acList = ObjectTestsHelper.fdidAcidValueList(oid, acidFdid);

        assert (acList.get(0).getValue().equalsIgnoreCase("WHATEVER 1"));
        assert (acList.get(1).getValue().equalsIgnoreCase("Whatever 2"));

        assertEquals(authDAO.findAll().size(), 4);

    }

    private int writeDummyObject() {
        Date d = new Date();
        edu.yale.library.ladybird.entity.Object object = new ObjectBuilder().setUserId(0).setProjectId(0).setDate(d).createObject();
        return new ObjectHibernateDAO().save(object);
    }

    /**
     * @return The update to be applied
     */
    private List<FieldDefinitionValue> getFdidValueList() {
        List<FieldDefinitionValue> fdidList = new ArrayList<>();
        fdidList.add(getAcidFdidValue(acidFdid, Arrays.asList("WHATEVER 1", "WHATEVER 2")));
        fdidList.add(getStringFdidValue(stringFdid, Arrays.asList("New String value 1", "New String value 2")));
        return fdidList;
    }

    /**
     * @return The update to be applied
     */
    private List<FieldDefinitionValue> getDropDownFdidValueList() {
        List<FieldDefinitionValue> fdidList = new ArrayList<>();
        fdidList.add(getAcidFdidValue(acidFdid, Arrays.asList("WHATEVER 1", "WHATEVER 2")));
        return fdidList;
    }

    private FieldDefinitionValue getAcidFdidValue(int fdid, List<String> value) {
        FieldDefinition fieldDefinition = new FieldDefinition(fdid);
        fieldDefinition.setType("dropdown");
        return new FieldDefinitionValue(fieldDefinition, value);
    }

    private FieldDefinitionValue getStringFdidValue(int fdid, List<String> value) {
        FieldDefinition fieldDefinition = new FieldDefinition(fdid);
        fieldDefinition.setType("string");
        return new FieldDefinitionValue(fieldDefinition, value);
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {

        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();
        ObjectStringDAO osDAO = new ObjectStringHibernateDAO();
        ObjectStringVersionDAO osvDAO = new ObjectStringVersionHibernateDAO();
        ObjectAcidVersionDAO oavDAO = new ObjectAcidVersionHibernateDAO();
        ObjectDAO objectDAO = new ObjectHibernateDAO();

        authDAO.deleteAll();
        osvDAO.deleteAll();
        oavDAO.deleteAll();
        oaDAO.deleteAll();
        osDAO.deleteAll();
        objectDAO.deleteAll();
    }

}

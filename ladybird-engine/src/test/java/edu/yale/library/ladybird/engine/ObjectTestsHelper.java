package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectVersionHibernateDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test util helper
 */
public class ObjectTestsHelper {

    public static int writeDummyObjString(int oid, int fdid, String value) {
        ObjectString objectString = new ObjectStringBuilder().setOid(oid).setFdid(fdid).setValue(value)
                .setDate(new Date()).createObjectString();
        ObjectStringDAO dao = new ObjectStringHibernateDAO();
        return dao.save(objectString);
    }

    public static int writeDummyObjAcid(int oid, int fdid, String value) {
        int acid = writeDummyAcid(fdid, value);
        ObjectAcid objectAcid = new ObjectAcidBuilder().setObjectId(oid).setFdid(fdid).setValue(acid)
                .setDate(new Date()).createObjectAcid();
        return new ObjectAcidHibernateDAO().save(objectAcid);
    }

    public static void writeDummyObjVersion(int[] versions) {
        List<ObjectVersion> dummObjects = new ArrayList<>();
        for (int v: versions) {
            dummObjects.add(getDummyVersion(1, 1, v));
        }
        ObjVersionBuilder.createVersion(dummObjects);
    }

    public static String fdidValue(int oid, int fdid) {
        ObjectStringDAO dao = new ObjectStringHibernateDAO();
        return dao.findByOidAndFdid(oid, fdid).getValue();
    }

    public static String fdidAcidValue(int oid, int fdid) {
        ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
        ObjectAcid oa  = objectAcidDAO.findByOidAndFdid(oid, fdid);
        int acid = oa.getValue();

        return new AuthorityControlHibernateDAO().findByAcid(acid).getValue();
    }

    public static int writeDummyAcid(int fdid, String value) {
        AuthorityControl authorityControl = new AuthorityControlBuilder().setFdid(fdid).setDate(new Date())
                .setValue(value).createAuthorityControl();
        return new AuthorityControlHibernateDAO().save(authorityControl);
    }

    public static ObjectVersion getDummyVersion(int oid, int uid, int vid) {
        return new ObjectVersionBuilder().setOid(oid).setUserId(uid).setVersionId(vid).createObjectVersion();
    }

    static class ObjVersionBuilder {
        static void createVersion(List<ObjectVersion> objectVersions) {
            ObjectVersionDAO objectVersionDAO = new ObjectVersionHibernateDAO();
            for (ObjectVersion o: objectVersions) {
                objectVersionDAO.save(o);
            }
        }
    }

}

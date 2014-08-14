package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectVersionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Helps version object, object string and object acid
 */
public class ObjectVersioner {

    private Logger logger = LoggerFactory.getLogger(ObjectVersioner.class);


    //TODO inject
    private ObjectStringVersionDAO objectStringVersionDAO = new ObjectStringVersionHibernateDAO();
    private ObjectAcidVersionDAO objectAcidVersionDAO = new ObjectAcidVersionHibernateDAO();
    private ObjectVersionDAO objectVersionDAO = new ObjectVersionHibernateDAO();

    /**
     * Versions list of ObjectString. New versions start with 1 (Perhaps should pass version).
     * @param list an object string value
     */
    public void versionObjectStrings(List<ObjectString> list) {
        for (ObjectString o : list) {
            try {
                ObjectStringVersion objStrVersion = new ObjectStringVersion(o);
                //logger.debug("Setting strings version={}", getLastObjectVersion(o.getOid()) + 1);
                objStrVersion.setVersionId(getLastObjectVersion(o.getOid()) + 1);
                objectStringVersionDAO.save(objStrVersion);
            } catch (Exception e) {
                logger.error("Error versioning object_string={}", o, e);
                throw e;
            }
        }
    }

    /**
     * Versions an objectAcid. new versions start with 1 (Perhaps should pass version).
     * @param list an object acid value
     */
    public void versionObjectAcid(List<ObjectAcid> list) {
        for (ObjectAcid o : list) {
            try {
                ObjectAcidVersion objectAcidVersion = new ObjectAcidVersion(o);
                objectAcidVersion.setVersionId(getLastObjectVersion(o.getObjectId()) + 1);
                objectAcidVersionDAO.save(objectAcidVersion);
            } catch (Exception e) {
                logger.error("Error versioning object_acid={}", o, e);
                throw e;
            }
        }
    }

    /**
     * Save object versions
     * @param oid oid
     * @param userId user id
     */
    public void versionObject(int oid, int userId) {
        final ObjectVersion objVersion = new ObjectVersionBuilder().setCreationDate(new Date())
                .setNotes("User Edit").setOid(oid).setUserId(userId)
                .setVersionId(getLastObjectVersion(oid) + 1).createObjectVersion();
        objectVersionDAO.save(objVersion);
    }


    //TODO
    /**
     * Save object versions
     * @param oid oid
     * @param userId user id
     */
    public void versionObject(int oid, int userId, String message) {
        final ObjectVersion objVersion = new ObjectVersionBuilder().setCreationDate(new Date())
                .setNotes(message).setOid(oid).setUserId(userId)
                .setVersionId(getLastObjectVersion(oid) + 1).createObjectVersion();
        objectVersionDAO.save(objVersion);
    }

    /**
     * Save object versions
     * @param oid oid
     * @param userId user id
     */
    public void versionObject(List<Integer> oid, int userId, String message) {
        for (Integer o: oid) {
            final ObjectVersion objVersion = new ObjectVersionBuilder().setCreationDate(new Date())
                    .setNotes(message).setOid(o).setUserId(userId)
                    .setVersionId(getLastObjectVersion(o) + 1).createObjectVersion();
            objectVersionDAO.save(objVersion);
        }
    }

    /**
     * Returns maximum version id
     *
     * @param oid object id
     * @return latet version or 0 if no version found
     */
    public int getLastObjectVersion(int oid) {
        return (objectVersionDAO.findByOid(oid).isEmpty()) ?  0 : objectVersionDAO.findMaxVersionByOid(oid);
    }
}

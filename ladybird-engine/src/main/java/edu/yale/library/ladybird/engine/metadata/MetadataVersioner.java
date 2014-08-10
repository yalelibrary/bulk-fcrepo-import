package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectVersionHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MetadataVersioner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO inject
    private ObjectStringVersionDAO objectStringVersionDAO = new ObjectStringVersionHibernateDAO();
    private ObjectAcidVersionDAO objectAcidVersionDAO = new ObjectAcidVersionHibernateDAO();
    private ObjectVersionDAO objectVersionDAO = new ObjectVersionHibernateDAO();
    private ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();


    /**
     * Updates and versions metadata
     * TODO acid size check
     */
    public void updateOidMetadata(int oid, int userId, List<FieldDefinitionValue> fieldDefinitionvalueList) {
        final MetadataVersioner metadataVersioner = new MetadataVersioner();
        final List<ObjectString> stringsToUpdate = new ArrayList<>();
        final List<ObjectAcid> objectAcidsToUpdate = new ArrayList<>();
        final List<ObjectString> stringsVersions = new ArrayList<>();
        final List<ObjectAcid> objectAcidVersions = new ArrayList<>();

        logger.trace("Updating oid metadata for oid={}", oid);

        try {
            for (FieldDefinitionValue field : fieldDefinitionvalueList) {
                int fdid = field.getFdid().getFdid();

                if (ObjectWriter.isString(fdid)) {
                    final ObjectString objStr = objectStringDAO.findByOidAndFdid(oid, fdid);

                    if (objStr == null) {
                        logger.error("Object string value null for oid={} fdid={}", oid, fdid);
                        continue;
                    }
                    //add to version list before updating
                    stringsVersions.add(new ObjectStringBuilder().setCopy(objStr).createObjectString2());

                    objStr.setValue(field.getValue());
                    stringsToUpdate.add(objStr);
                } else { //assuming acid!
                    final ObjectAcid objAcid = objectAcidDAO.findByOidAndFdid(oid, field.getFdid().getFdid());

                    //add to version list before updating
                    objectAcidVersions.add(new ObjectAcidBuilder().setO(objAcid).createObjectAcid2());

                    int acidInt = objAcid.getValue();
                    final AuthorityControl oldAcid = authorityControlDAO.findByAcid(acidInt);

                    //update only if the field has been changed
                    if (!oldAcid.getValue().equalsIgnoreCase(field.getValue())) {
                        //TODO out of tx (coordinate with objectAcidsToUpdate)
                        final AuthorityControl newAuthorityControl = new AuthorityControlBuilder()
                                .setAc(oldAcid).createAuthorityControl2();
                        newAuthorityControl.setValue(field.getValue());
                        int newAcidInt = authorityControlDAO.save(newAuthorityControl);

                        //set object acid to point to this new acid
                        objAcid.setValue(newAcidInt);
                        //1. make sure to write this object acid:
                        objectAcidsToUpdate.add(objAcid);
                    }
                }
            }
            //Save and update list (do this in a multi business object transaction)
            objectAcidDAO.saveOrUpdateList(objectAcidsToUpdate);
            objectStringDAO.saveOrUpdateList(stringsToUpdate);

            metadataVersioner.versionAcid(objectAcidVersions);
            metadataVersioner.versionStrings(stringsVersions);
            metadataVersioner.versionObject(oid, userId);
        } catch (Exception e) {
            logger.error("Error updating or versioning values for oid={}", oid, e);
            throw e;
        }
    }

    /**
     * Versions list of ObjectString. New versions start with 1 (Perhaps should pass version).
     * @param list an object string value
     */
    public void versionStrings(List<ObjectString> list) {
        for (ObjectString o : list) {
            try {
                ObjectStringVersion objStrVersion = new ObjectStringVersion(o);
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
    public void versionAcid(List<ObjectAcid> list) {
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

package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
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

public class Rollbacker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO inject
    private ObjectStringVersionDAO objectStringVersionDAO = new ObjectStringVersionHibernateDAO();
    private ObjectAcidVersionDAO objectAcidVersionDAO = new ObjectAcidVersionHibernateDAO();
    private ObjectVersionDAO objectVersionDAO = new ObjectVersionHibernateDAO();
    private ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private FieldDefinitionDAO fdidDAO = new FieldDefinitionHibernateDAO();

    /**
     * Rolls back an object. Older copy is perserved.
     * TODO tx handling
     * TODO version id control
     */
    public void rollback(int oid, int version, int userId) {
        ObjectVersioner objVersioner = new ObjectVersioner();

        try {
            //1. Version the current instance
            final List<FieldDefinition> flist = fdidDAO.findAll();
            final List<ObjectString> archiveStrings = new ArrayList<>();
            final List<ObjectAcid> archiveAcids = new ArrayList<>();

            for (FieldDefinition f : flist) {
                int fdid = f.getFdid();

                if (isString(fdid)) {
                    List<ObjectString> osList = objectStringDAO.findListByOidAndFdid(oid, f.getFdid());
                    if (osList.isEmpty()) {
                        logger.debug("Empty os list for oid={} fdid={}", oid, fdid);
                    } else {
                        for (ObjectString os: osList) {
                            archiveStrings.add(new ObjectStringBuilder().setCopy(os).createObjectString2());
                        }
                    }
                } else {
                    List<ObjectAcid> oaList = objectAcidDAO.findListByOidAndFdid(oid, fdid);
                    if (oaList.isEmpty()) {
                        logger.debug("Empty oa list for oid={} fdid={}", oid, fdid);
                    } else {
                        for (ObjectAcid oa: oaList) {
                            archiveAcids.add(new ObjectAcidBuilder().setO(oa).createObjectAcid2());
                        }
                    }
                }
            }

            objVersioner.versionObjectAcid(archiveAcids);
            objVersioner.versionObjectStrings(archiveStrings);

            //3. Replace object string and acid values with version's string and acid values
            List<FieldDefinition> fList = fdidDAO.findAll();
            List<ObjectString> objStrToUpdate = new ArrayList<>();
            List<ObjectAcid> objAcidToUpdate = new ArrayList<>();

            for (FieldDefinition f : fList) {
                final int fdid = f.getFdid();

                if (isString(fdid)) {
                    List<ObjectStringVersion> osvList = objectStringVersionDAO.findListByOidAndFdidAndVersion(oid, fdid, version);

                    if (osvList.isEmpty()) {
                        logger.debug("No corresponding version for this oid={} and fdid={} and version={}", oid, fdid, version);
                        //logger.trace("Full list={}", objectStringVersionDAO.findAll());
                    }

                    //delete former entries and add new ones. We could've updated but there might be a mismatch between
                    //versioned entries number and the current number

                    List<ObjectString> osList = objectStringDAO.findListByOidAndFdid(oid, fdid);
                    objectStringDAO.delete(osList);

                    for (ObjectStringVersion osv: osvList) {
                        ObjectString os = new ObjectStringBuilder().setOid(osv.getOid()).setFdid(osv.getFdid())
                                .setValue(osv.getValue()).setDate(osv.getDate()).setUserId(osv.getUserId()).createObjectString();
                        objStrToUpdate.add(os);
                    }
                } else { //an acid
                    List<ObjectAcidVersion> oavList = objectAcidVersionDAO.findListByOidAndFdidAndVersion(oid, fdid, version);

                    if (oavList.isEmpty()) {
                        logger.debug("No corresponding version for this oid={} and fdid={} and version={}", oid, fdid, version);
                        //logger.trace("Full list={}", oavList);
                    }

                    //delete and add new object acids
                    List<ObjectAcid> oaList = objectAcidDAO.findListByOidAndFdid(oid, fdid);
                    objectAcidDAO.delete(oaList);

                    for (ObjectAcidVersion oav: oavList) {
                        ObjectAcid oa = new ObjectAcidBuilder().setObjectId(oid).setFdid(fdid).setDate(oav.getDate()).setUserId(oav.getUserId()).setValue(oav.getValue()).createObjectAcid();
                        objAcidToUpdate.add(oa);
                    }
                }
            }
            //4. Hit the dao, update the lists
            objectStringDAO.saveList(objStrToUpdate);
            objectAcidDAO.saveList(objAcidToUpdate);

            //5. Create a new ObjectVersion. Note that the version Id must correspond to version acid and version string.
            // Currently done in the version methods.(Doing this before can trip up the version number)
            final ObjectVersion objectVersion = new ObjectVersionBuilder()
                    .setVersionId(objVersioner.getLastObjectVersion(oid) + 1)
                    .setCreationDate(new Date()).setNotes("Rollback").setOid(oid).setUserId(userId)
                    .createObjectVersion();
            objectVersionDAO.save(objectVersion);
        } catch (Exception e) {
            logger.error("Error rolling back object={}", oid);
            throw e;
        }
    }

    public boolean isString(int fdid) {
        return FieldConstantUtil.isString(fdid);
    }
}

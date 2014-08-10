package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
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
        MetadataVersioner metadataVersioner = new MetadataVersioner();

        try {
            //2. Version the current instance
            final List<FieldDefinition> flist = fdidDAO.findAll();
            final List<ObjectString> archiveStrings = new ArrayList<>();
            final List<ObjectAcid> archiveAcids = new ArrayList<>();

            for (FieldDefinition f : flist) {
                ObjectString os = objectStringDAO.findByOidAndFdid(oid, f.getFdid());
                if (os != null) {
                    archiveStrings.add(new ObjectStringBuilder().setCopy(os).createObjectString2());
                } else {
                    logger.trace("No string val for oid={} fdid={}", oid, f.getFdid());
                }

                ObjectAcid objAcid = objectAcidDAO.findByOidAndFdid(oid, f.getFdid());

                if (objAcid != null) {
                    archiveAcids.add(new ObjectAcidBuilder().setO(objAcid).createObjectAcid2());
                } else {
                    logger.trace("No acid val for oid={} fdid={}", oid, f.getFdid());
                }
            }

            logger.trace("Archive acids size={}", archiveAcids.size());
            logger.trace("Archive strings size={}", archiveStrings.size());

            metadataVersioner.versionAcid(archiveAcids);
            metadataVersioner.versionStrings(archiveStrings);

            logger.trace("Done archiving current instance");

            //3. Replace object string and acid values with version's string and acid values
            List<FieldDefinition> list = fdidDAO.findAll();
            List<ObjectString> objStrToUpdate = new ArrayList<>();
            List<ObjectAcid> objAcidToUpdate = new ArrayList<>();

            for (FieldDefinition f : list) {
                int fdid = f.getFdid();

                if (ObjectWriter.isString(f.getFdid())) {
                    ObjectStringVersion historyObject = objectStringVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);
                    ObjectString objectString1 = objectStringDAO.findByOidAndFdid(oid, fdid);
                    objectString1.setValue(historyObject.getValue());
                    objStrToUpdate.add(objectString1);
                } else { //an acid
                    ObjectAcidVersion historyObjectAcid = objectAcidVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);
                    ObjectAcid objectAcid1 = objectAcidDAO.findByOidAndFdid(oid, fdid);
                    objectAcid1.setValue(historyObjectAcid.getValue());
                    objAcidToUpdate.add(objectAcid1);
                }
            }
            //4. Hit the dao, update the lists
            objectStringDAO.saveOrUpdateList(objStrToUpdate);
            objectAcidDAO.saveOrUpdateList(objAcidToUpdate);

            //5. Create a new ObjectVersion. Note that the version Id must correspond to version acid and version string.
            // Currently done in the version methods.(Doing this before can trip up the version number)
            final ObjectVersion objectVersion = new ObjectVersionBuilder().setVersionId(metadataVersioner.getLastObjectVersion(oid) + 1)
                    .setCreationDate(new Date()).setNotes("Rollback").setOid(oid).setUserId(userId)
                    .createObjectVersion();
            objectVersionDAO.save(objectVersion);
        } catch (Exception e) {
            throw e;
        }
    }
}

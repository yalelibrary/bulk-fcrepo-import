package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MetadataEditor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();
    ObjectVersioner metadataEditor = new ObjectVersioner();

    /**
     * Updates and versions metadata
     * TODO acid size check
     */
    public void updateOidMetadata(int oid, int userId, List<FieldDefinitionValue> fieldDefinitionvalueList) {
        final List<ObjectString> stringsToUpdate = new ArrayList<>();
        final List<ObjectAcid> objectAcidsToUpdate = new ArrayList<>();
        final List<AuthorityControl> acidsToUpdate = new ArrayList<>();
        final List<ObjectString> stringsVersions = new ArrayList<>();
        final List<ObjectAcid> objectAcidVersions = new ArrayList<>();

        try {
            for (FieldDefinitionValue field : fieldDefinitionvalueList) {

                int fdid = field.getFdid().getFdid();

                if (isString(fdid)) {
                    final List<ObjectString> objectStrings = objectStringDAO.findListByOidAndFdid(oid, fdid);

                    for (ObjectString os : objectStrings) {
                        stringsVersions.add(new ObjectStringBuilder().setCopy(os).createObjectString2());
                    }

                    //N.B. assuming order is preserved. Perhaps the fields should have a zindex or something.
                    List<String> multiFieldValues = field.getValue();

                    if (multiFieldValues.size() != objectStrings.size()) {
                        logger.error("Size mismatch! Skipping oid={} fdid={}. Multi field values size={} obj str size={}",
                                oid, fdid, multiFieldValues.size(), objectStrings.size());
                        continue;
                    }

                    for (int i = 0; i < objectStrings.size(); i++) {
                        objectStrings.get(i).setValue(multiFieldValues.get(i));
                        stringsToUpdate.add(objectStrings.get(i));
                    }
                } else { //an acid:
                    final List<ObjectAcid> objectAcids = objectAcidDAO.findListByOidAndFdid(oid, fdid);

                    //version it:
                    for (ObjectAcid objectAcid : objectAcids) {
                        objectAcidVersions.add(new ObjectAcidBuilder().setO(objectAcid).createObjectAcid2());
                    }

                    //assuming order is preserved
                    List<String> multiFieldsValues = field.getValue();

                    if (multiFieldsValues.size() != objectAcids.size()) {
                        logger.debug("Size mismatch! Skipping oid={}  fdid={}.  Multi field values size={} obj str size={}",
                                oid, fdid, multiFieldsValues.size(), objectAcids.size());
                        continue;
                    }

                    //look up authority control, and add a new acid
                    for (int i = 0; i < objectAcids.size(); i++) {
                        AuthorityControl oldAcid = authorityControlDAO.findByAcid(objectAcids.get(i).getValue());

                        if (!oldAcid.getValue().equalsIgnoreCase(multiFieldsValues.get(i))) {
                            final AuthorityControl newAuthorityControl = new AuthorityControlBuilder().setAc(oldAcid)
                                    .createAuthorityControl2();
                            newAuthorityControl.setValue(multiFieldsValues.get(i));
                            int newAcidInt = authorityControlDAO.save(newAuthorityControl);
                            objectAcids.get(i).setValue(newAcidInt);
                            //1. make sure to write this object acid:
                            objectAcidsToUpdate.add(objectAcids.get(i));
                        }
                    }
                }
            }

            logger.debug("Updating oid metadata for oid={}", oid);

            logger.debug("Acid List to be updated={}", acidsToUpdate);
            authorityControlDAO.saveOrUpdateList(acidsToUpdate);

            logger.debug("Object String to be updated={}", stringsToUpdate);
            objectStringDAO.saveOrUpdateList(stringsToUpdate);

            logger.debug("Should save list={}", stringsToUpdate);
            objectAcidDAO.saveOrUpdateList(objectAcidsToUpdate);

            metadataEditor.versionObjectAcid(objectAcidVersions);
            metadataEditor.versionObjectStrings(stringsVersions);
            metadataEditor.versionObject(oid, userId);
        } catch (Exception e) {
            logger.error("Error updating or versioning values for oid={}", oid, e);
            throw e;
        }
    }

    public boolean isString(int fdid) {
        return FieldConstantUtil.isString(fdid);
    }

}

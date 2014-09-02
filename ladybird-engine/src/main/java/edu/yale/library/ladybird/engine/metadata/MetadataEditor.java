package edu.yale.library.ladybird.engine.metadata;

import com.google.common.base.Preconditions;
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

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MetadataEditor {
    private final Logger logger = getLogger(this.getClass());

    private final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private final AuthorityControlDAO acidDAO = new AuthorityControlHibernateDAO();
    private final ObjectVersioner metadataEditor = new ObjectVersioner();

    /**
     * Updates and versions metadata
     */
    public void updateOidMetadata(final int oid, final int userId,
                                  final List<FieldDefinitionValue> fieldDefinitionvalueList) {
        final List<ObjectString> stringsToUpdate = new ArrayList<>();
        final List<ObjectAcid> objectAcidsToUpdate = new ArrayList<>();
        final List<AuthorityControl> acidsToUpdate = new ArrayList<>();
        final List<ObjectString> stringsVersions = new ArrayList<>();
        final List<ObjectAcid> objectAcidVersions = new ArrayList<>();

        try {
            for (final FieldDefinitionValue field : fieldDefinitionvalueList) {
                final int fdid = field.getFdid().getFdid();

                logger.trace("Eval={}", fdid);

                if (isString(fdid)) {
                    final List<ObjectString> objectStrings = objectStringDAO.findListByOidAndFdid(oid, fdid);

                    for (final ObjectString os : objectStrings) {
                        stringsVersions.add(new ObjectStringBuilder().setCopy(os).createObjectString2());
                    }

                    //N.B. assuming order is preserved. Perhaps the fields should have a zindex or something.
                    final List<String> multiFdidValue = field.getValue();

                    if (multiFdidValue.size() != objectStrings.size()) {
                        logger.error("Size mismatch! Skipping oid={} fdid={}. Multi field values size={} obj str size={}",
                                oid, fdid, multiFdidValue.size(), objectStrings.size());
                        continue;
                    }

                    for (int i = 0; i < objectStrings.size(); i++) {
                        objectStrings.get(i).setValue(multiFdidValue.get(i));
                        stringsToUpdate.add(objectStrings.get(i));
                    }
                } else { //an acid:
                    final List<ObjectAcid> objectAcids = objectAcidDAO.findListByOidAndFdid(oid, fdid);

                    //version it:
                    for (final ObjectAcid objectAcid : objectAcids) {
                        objectAcidVersions.add(new ObjectAcidBuilder().setO(objectAcid).createObjectAcid2());
                    }

                    //assuming order is preserved
                    final List<String> multiFdidValue = field.getValue();

                    if (multiFdidValue.size() != objectAcids.size()) {
                        logger.debug("Size mismatch! Skipping oid={}  fdid={}.  Multi field values size={} obj str size={}",
                                oid, fdid, multiFdidValue.size(), objectAcids.size());
                        continue;
                    }

                    //look up authority control, and add a new acid
                    for (int i = 0; i < objectAcids.size(); i++) {
                        if (multiFdidValue.get(i) == null) {
                            logger.debug("Skipping fdid={} due to null value", fdid);
                            continue;
                        }

                        final AuthorityControl oldAcid = acidDAO.findByAcid(objectAcids.get(i).getValue());

                        if (!oldAcid.getValue().equalsIgnoreCase(multiFdidValue.get(i))) {
                            if (multiFdidValue.get(i) == null) {
                                logger.debug("Null value supplied");
                            }

                            final List<AuthorityControl> exAcids = acidDAO.findByFdidAndStringValue(fdid, multiFdidValue.get(i));

                            //don't add a new acid if the field is dropdown. This means that object acid merely needs to be switched

                            if (FieldConstantUtil.isDropDown(field.getFdid()) && !exAcids.isEmpty()) {
                                logger.debug("Switching, not adding new authority control for drop down acid");

                                logger.debug("Existing acid size={}", exAcids.size());

                                Preconditions.checkState(exAcids.size() == 1,
                                        "Only once acid should exist at this point for" + fdid + "and val" + multiFdidValue.get(i));

                                objectAcids.get(i).setValue(exAcids.get(0).getAcid());
                            } else {
                                final AuthorityControl newAcid = new AuthorityControlBuilder().setAc(oldAcid)
                                        .createAuthorityControl2();
                                newAcid.setValue(multiFdidValue.get(i));
                                final int newAcidInt = acidDAO.save(newAcid);
                                objectAcids.get(i).setValue(newAcidInt);
                            }

                            //make sure to write this object acid:
                            objectAcidsToUpdate.add(objectAcids.get(i));
                        }
                    }
                }
            }

            logger.trace("Updating oid metadata for oid={}", oid);
            logger.trace("Acid List to be updated={}", acidsToUpdate);
            logger.trace("Object String to be updated={}", stringsToUpdate);
            logger.trace("Should save list={}", stringsToUpdate);

            acidDAO.saveOrUpdateList(acidsToUpdate);
            objectStringDAO.saveOrUpdateList(stringsToUpdate);
            objectAcidDAO.saveOrUpdateList(objectAcidsToUpdate);

            metadataEditor.versionObjectAcid(objectAcidVersions);
            metadataEditor.versionObjectStrings(stringsVersions);
            metadataEditor.versionObject(oid, userId);
        } catch (Exception e) {
            logger.error("Error updating or versioning values for oid={}", oid, e);
            throw e;
        }
    }

    private boolean isString(int fdid) {
        return FieldConstantUtil.isString(fdid);
    }

}

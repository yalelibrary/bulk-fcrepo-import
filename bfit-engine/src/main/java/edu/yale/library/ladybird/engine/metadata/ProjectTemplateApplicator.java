package edu.yale.library.ladybird.engine.metadata;


import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateStringsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ProjectTemplateStringsHibernateDAO;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProjectTemplateApplicator {

    private static final Logger logger = getLogger(ProjectTemplateApplicator.class);

    private final ObjectDAO objectDAO = new ObjectHibernateDAO();

    private final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();

    private final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();

    private final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();

    private final FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();

    private final ProjectTemplateStringsDAO templateStringsDAO = new ProjectTemplateStringsHibernateDAO();

    private final ObjectVersioner metadataEditor = new ObjectVersioner();

    // Note 1: Original schema has ProjectTemplateAcid and it seems there's no use for that table.
    // Note 2: All oids that belong to a project are updated.
    //TODO Check if acid value is locked.
    public void applyTemplate(final ProjectTemplate projectTemplate, final int userId) {
        final List<ObjectString> stringsVersions = new ArrayList<>();
        final List<ObjectAcid> objectAcidVersions = new ArrayList<>();
        final List<Integer> oidVersions = new ArrayList<>();
        final int templateId = projectTemplate.getTemplateId();

        try {
            final List<Object> objectList = objectDAO.findByProject(projectTemplate.getProjectId());
            final List<FieldDefinition> fieldDefinitions = fieldDefinitionDAO.findAll();

            /* Must do this since even a single field application requires all metadata fields to be versioned.
               If they are not versioned the fields will be blank when rolled back */
            if (nullProjectTemplate(projectTemplate)) {
                logger.error("Ignoring project template={} application for user={}. "
                        + "The project template does not have any values.", templateId, userId);
                return;
            }

            //For each of the fdids, read the template value and append to oid string and authority control value:
            for (final Object o : objectList) {
                final int oid = o.getOid();

                for (final FieldDefinition fieldDef : fieldDefinitions) {

                    try {
                        final int fdid = fieldDef.getFdid();

                        logger.debug("Eval oid={} fdid={}", oid, fdid);

                        final boolean isMultivalued = fieldDef.isMultivalue();

                        //1. string
                        if (FieldConstantUtil.isString(fdid)) {
                            final ProjectTemplateStrings pString = templateStringsDAO.findByFdidAndTemplateId(fieldDef.getFdid(), templateId);
                            final String templateStr = pString.getValue();

                            ObjectString objectString = objectStringDAO.findByOidAndFdid(oid, fdid);
                            stringsVersions.add(new ObjectString(objectString)); //add for versioning

                            if (templateStr != null && !templateStr.isEmpty()) {

                                ObjectString newObjectString = new ObjectStringBuilder().setValue(templateStr).setDate(new Date()).setFdid(fdid).setOid(oid).createObjectString();
                                //objectString.setValue(objectString.getValue() + templateStr); //wrong because a new value should be added?
                                //objectStringDAO.updateItem(objectString);
                                objectStringDAO.saveOrUpdateItem(newObjectString);
                                logger.debug("Object strings for oid={} fdid={} are={}", oid, fdid, objectStringDAO.findByOidAndFdid(oid, fdid));
                            }
                        } else { //2. acid
                            ObjectAcid existingObjectAcid = null;

                            if (!isMultivalued) {
                                List<ObjectAcid> list = objectAcidDAO.findListByOidAndFdid(oid, fdid);
                                checkState(list.size() < 2);
                                checkState(!list.isEmpty(), "Existing object acid must not be empty");
                                existingObjectAcid = list.get(0);
                            }

                            final ProjectTemplateStrings pString = templateStringsDAO.findByFdidAndTemplateId(fdid, templateId);
                            final String templateValue = pString.getValue();

                            objectAcidVersions.addAll(getOidObjectAcid(oid, fdid));

                            //Add a new acid:
                            if (templateValue != null && !templateValue.isEmpty()) {

                                //N.B. Don't add an acid if an acid exists:

                                List<AuthorityControl> existingAcid = authorityControlDAO.findByFdidAndStringValue(fdid, templateValue);
                                logger.debug("existing acids={}", existingAcid.toString());

                                int acid;

                                if (existingAcid.isEmpty()) {
                                    final AuthorityControl newAcid = new AuthorityControlBuilder().setFdid(fdid).setDate(new Date())
                                            .setValue(templateValue).createAuthorityControl();
                                    acid = authorityControlDAO.save(newAcid);
                                } else {
                                    checkState(existingAcid.size() == 1, "More than one acid found. Acids are supposed to be unique for fdid=" + fdid + " and value=" + templateValue);
                                    acid = existingAcid.get(0).getAcid();
                                }

                                final ObjectAcid objAcid = new ObjectAcidBuilder().setDate(new Date()).setUserId(userId)
                                        .setValue(acid).setObjectId(oid).setFdid(fdid).createObjectAcid();
                                objectAcidDAO.save(objAcid);

                                //Remove old object acid if the fdid is not multi-valued:
                                if (!isMultivalued) {
                                    objectAcidDAO.delete(Collections.singletonList(existingObjectAcid));
                                    checkState(objectAcidDAO.findListByOidAndFdid(oid, fdid).size() == 1, "Only one object acid should exist at this point!");
                                } else {
                                    checkState(objectAcidDAO.findListByOidAndFdid(oid, fdid).size() > 1, "Only one object acid found. Expecting more.");
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error applying template value for fdid={}. Skipping fdid for oid={}.", fieldDef, oid, e);
                    }
                } //end:for
                oidVersions.add(oid);
            }
            //version objects:
            metadataEditor.versionObjectAcid(objectAcidVersions);
            metadataEditor.versionObjectStrings(stringsVersions);
            metadataEditor.versionObject(oidVersions, userId, "PROJECT TEMPLATE APPLICATION");
        } catch (Exception e) {
            logger.error("Error applying project template={}", projectTemplate, e); //TODO no individual obj exception.
            throw e;
        }
    }

    /**
     * Looks up full string value
     *
     * @param oid  oid
     * @param fdid fdid
     * @return corresponding AuthorityControl item
     */
    private List<ObjectAcid> getOidObjectAcid(int oid, int fdid) {
        return objectAcidDAO.findListByOidAndFdid(oid, fdid);
    }

    public boolean nullProjectTemplate(ProjectTemplate projectTemplate) {
        return templateStringsDAO.findByTemplateId(projectTemplate.getTemplateId()).isEmpty(); //TODO DAO count method
    }
}

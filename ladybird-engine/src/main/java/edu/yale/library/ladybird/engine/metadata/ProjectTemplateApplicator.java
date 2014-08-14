package edu.yale.library.ladybird.engine.metadata;


import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
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
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ProjectTemplateApplicator {

    private static final Logger logger = getLogger(ProjectTemplateApplicator.class);

    //TODO inject DAO(s)
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
                    final int fdid = fieldDef.getFdid();

                    //1. string
                    if (ObjectWriter.isString(fdid)) {
                        final ProjectTemplateStrings pString = templateStringsDAO.findByFdidAndTemplateId(fieldDef.getFdid(), templateId);
                        final String templateValue = pString.getValue();

                        ObjectString objectString = objectStringDAO.findByOidAndFdid(oid, fdid);
                        stringsVersions.add(new ObjectString(objectString)); //add for versioning

                        if (templateValue != null && !templateValue.isEmpty()) {
                            objectString.setValue(objectString.getValue() + templateValue); //wrong because a new value should be added?
                            objectStringDAO.updateItem(objectString);
                        }
                    } else { //2. acid
                        final ProjectTemplateStrings pString = templateStringsDAO.findByFdidAndTemplateId(fdid, templateId);
                        final String templateValue = pString.getValue();

                        objectAcidVersions.addAll(getOidObjectAcid(oid, fdid));

                        //Add a new acid:
                        if (templateValue != null && !templateValue.isEmpty()) {
                            final AuthorityControl newAcid = new AuthorityControlBuilder().setFdid(fdid).setDate(new Date())
                                    .setValue(templateValue).createAuthorityControl();
                            int newAcidInt = authorityControlDAO.save(newAcid);

                            final ObjectAcid objAcid = new ObjectAcidBuilder().setDate(new Date()).setUserId(userId)
                                    .setValue(newAcidInt).setObjectId(oid).setFdid(fdid).createObjectAcid();
                            objectAcidDAO.save(objAcid);
                        }
                    }
                }
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

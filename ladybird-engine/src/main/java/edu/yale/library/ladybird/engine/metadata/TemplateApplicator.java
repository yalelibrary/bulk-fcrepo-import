package edu.yale.library.ladybird.engine.metadata;


import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
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

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class TemplateApplicator {

    private static final Logger logger = getLogger(TemplateApplicator.class);

    //TODO inject DAO(s)
    private final ObjectDAO objectDAO = new ObjectHibernateDAO();
    private final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    private final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();
    private final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    private final FieldDefinitionDAO fieldDefinitionDAO = new FieldDefinitionHibernateDAO();
    private final ProjectTemplateStringsDAO projectTemplateStringsDAO = new ProjectTemplateStringsHibernateDAO();

    //TODO must version items as well
    //TODO object size check (If too many updates are applied this may crash the system.)
    public void updateObjectMetadata(final ProjectTemplate projectTemplate) {
        try {
            final int templateId = projectTemplate.getTemplateId();
            final List<Object> objectList = objectDAO.findByProject(projectTemplate.getProjectId());
            final List<FieldDefinition> fieldDefinitions = fieldDefinitionDAO.findAll();

            //For each of the fdids, read the template value and append to oid string and authority control value:

            for (Object o: objectList) {
                final int oid = o.getOid();

                for (final FieldDefinition fieldDef: fieldDefinitions) {
                    final int fdid = fieldDef.getFdid();

                    logger.trace("Eval. val for fdid={} and template={}", fdid, templateId);

                    if (ObjectWriter.isString(fieldDef.getFdid())) {
                        ProjectTemplateStrings pString = projectTemplateStringsDAO
                                .findByFdidAndTemplateId(fieldDef.getFdid(), templateId);

                        final String templateValue = pString.getValue();

                        if (templateValue == null || templateValue.isEmpty()) {
                            continue;
                        }

                        ObjectString objectString = objectStringDAO.findByOidAndFdid(oid, fdid);
                        objectString.setValue(objectString.getValue() + templateValue);
                        objectStringDAO.updateItem(objectString);
                    } else {
                        //TODO Check if acid value is locked.
                        // Note Original schema has ProjectTemplateAcid and it seems there's no use for that table. Hence
                        // the value to be written the same. We just need to do see if an ACID is locked.
                        ProjectTemplateStrings pString = projectTemplateStringsDAO
                                .findByFdidAndTemplateId(fdid, templateId);
                        final String templateValue = pString.getValue();

                        if (templateValue == null || templateValue.isEmpty()) {
                            continue;
                        }

                        ObjectAcid objectAcid = objectAcidDAO.findByOidAndFdid(oid, fdid);
                        int acidValue = objectAcid.getValue();

                        AuthorityControl acid = authorityControlDAO.findByAcid(acidValue);

                        if (acid.getValue() == null) {
                            logger.error("Null value for acid={}. Skipping.", acid.getAcid());
                            continue; //TODO ignore
                        }

                        acid.setValue(acid.getValue() + templateValue);
                        authorityControlDAO.updateItem(acid);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error updating metadata for objects.", e); //Note: no individual object exception. TODO
            throw e;
        }
    }
}

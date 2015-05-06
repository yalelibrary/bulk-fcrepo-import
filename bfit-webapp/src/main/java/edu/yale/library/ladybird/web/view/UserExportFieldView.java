
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldExportOptionsDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@ManagedBean
@RequestScoped
public class UserExportFieldView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private UserProjectFieldExportOptionsDAO exportDAO;

    @Inject
    private FieldDefinitionDAO fieldDefintionDAO;

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();

        final int userId;
        final int projectId;
        try {
            userId = authUtil.getCurrentUserId();
            projectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();
        } catch (Exception e) {
            logger.trace("No current user or default project"); //ignore
            return;
        }

        fieldDefintionExports = new ArrayList<>();

        try {
            logger.debug("Init UserExportFieldView bean");

            List<FieldDefinition> list = fieldDefintionDAO.findAll();

            for (FieldDefinition f : list) {
                //check to see if an entry exists in the user export options or profile table

                if (exportDAO.findByUserAndProjectAndFdid(userId, projectId, f.getFdid()) != null) {
                    fieldDefintionExports.add(new FieldDefinitionExport(f, true));
                } else {
                    fieldDefintionExports.add(new FieldDefinitionExport(f, false));
                }
            }
        } catch (Exception e) {
            logger.error("Error setting items", e);
        }
    }


    public String save() {
        logger.debug("Changing user export fields={}", fieldDefintionExports.toString());

        final int userId = authUtil.getCurrentUserId();
        final int projectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();


        try {
            for (FieldDefinitionExport f : fieldDefintionExports) {
                if (f.enabled) {
                    UserProjectFieldExportOptions opt = new UserProjectFieldExportOptions();
                    opt.setFdid(f.getFieldDefinition().getFdid());
                    opt.setProjectId(projectId);
                    opt.setUserId(userId);
                    exportDAO.save(opt);
                } else {
                    //remove the entry if any
                    UserProjectFieldExportOptions opt = exportDAO.findByUserAndProjectAndFdid(userId, projectId, f.getFieldDefinition().getFdid());
                    if (opt != null) {
                        logger.debug("Deleting entry={}", opt.toString());
                        exportDAO.delete(Collections.singletonList(opt));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error updating entity", e);
            return fail();
        }
        return ok();
    }

    private List<FieldDefinitionExport> fieldDefintionExports;

    public List<FieldDefinitionExport> getFieldDefintionExports() {
        return fieldDefintionExports;
    }

    public void setFieldDefintionExports(List<FieldDefinitionExport> fieldDefintionExports) {
        this.fieldDefintionExports = fieldDefintionExports;
    }

    public class FieldDefinitionExport {
        FieldDefinition fieldDefinition;
        boolean enabled;

        public FieldDefinitionExport(FieldDefinition fieldDefinition, boolean enabled) {
            this.fieldDefinition = fieldDefinition;
            this.enabled = enabled;
        }

        public FieldDefinition getFieldDefinition() {
            return fieldDefinition;
        }

        public void setFieldDefinition(FieldDefinition fieldDefinition) {
            this.fieldDefinition = fieldDefinition;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return "FieldDefinitionExport{"
                    + "fieldDefinition=" + fieldDefinition
                    + ", enabled=" + enabled
                    + '}';
        }
    }

}



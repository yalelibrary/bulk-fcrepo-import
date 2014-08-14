
package edu.yale.library.ladybird.web.view.template;

import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateStringsDAO;
import edu.yale.library.ladybird.web.view.AbstractView;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Views current values for a specific template
 * @see ProjectTemplateView for addition of this template
 * @see TemplatePopulateView for initial populating of values
 * @see TemplateUpdateView for changing values
 */
@ManagedBean
@ViewScoped
public class TemplateEditView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private ProjectTemplate item = new ProjectTemplateBuilder().createProjectTemplate();

    private List<FieldDefinitionValue> fieldDefinitionvalueList;

    @Inject
    private ProjectTemplateDAO projectTemplateDAO;

    @Inject
    private ProjectTemplateAcidDAO projectTemplateAcidDAO; //not used

    @Inject
    private ProjectTemplateStringsDAO projectTemplateStringsDAO;

    @Inject
    private FieldDefinitionDAO fieldDefinitonDAO;

    @PostConstruct
    public void init() {
        logger.trace("Init ProjectTemplate Metadata Edit View");
        initFields();
        dao = projectTemplateDAO;
        fieldDefinitionvalueList = loadFieldDefinitions();
    }

    /**
     * Loads by project template id.
     * N.B. Needs project template id.
     * @return field definition list.
     */
    private List<FieldDefinitionValue> loadFieldDefinitions() {
        List<FieldDefinitionValue> fieldDefinitionvalueList = new ArrayList<>();
        int templateId;

        try {
            //N.B.
            if (Faces.getRequestParameter("project_template_id") == null) {
                templateId = Integer.parseInt(Faces.getRequestParameter("id"));
            } else {
                templateId = Integer.parseInt(Faces.getRequestParameter("project_template_id"));
            }

            List<FieldDefinition> list = fieldDefinitonDAO.findAll();

            for (FieldDefinition f : list) {
                ProjectTemplateStrings projectTemplateString = projectTemplateStringsDAO.findByFdidAndTemplateId(f.getFdid(), templateId);
                fieldDefinitionvalueList.add(new FieldDefinitionValue(f, projectTemplateString.getValue()));
            }
        } catch (Exception e) {
            logger.debug("Error loading fdid value list. {}", e);
        }

        return fieldDefinitionvalueList;
    }

    public ProjectTemplate getItem() {
        return item;
    }

    public void setItem(ProjectTemplate item) {
        this.item = item;
    }

    public List<FieldDefinitionValue> getFieldDefinitionvalueList() {
        return fieldDefinitionvalueList;
    }

    public void setFieldDefinitionvalueList(List<FieldDefinitionValue> fieldDefinitionvalueList) {
        this.fieldDefinitionvalueList = fieldDefinitionvalueList;
    }

}



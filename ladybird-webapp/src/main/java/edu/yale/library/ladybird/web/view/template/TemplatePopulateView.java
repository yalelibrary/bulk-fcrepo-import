
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
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Used for populating a newly added template.
 * @see ProjectTemplateView for addition of this template
 * @see TemplateEditView for viewing values for this template
 * @see TemplateUpdateView for updating values
 */
@ManagedBean
@RequestScoped
public class TemplatePopulateView extends AbstractView {
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
        logger.trace("Init ProjectTemplate Metadata View");
        initFields();
        dao = projectTemplateDAO;

        if (fieldDefinitionvalueList == null) {
            logger.trace("Field definition value null");
            fieldDefinitionvalueList = buildFieldDefnList();
        }
    }

    private List<FieldDefinitionValue> buildFieldDefnList() {
        final List<FieldDefinitionValue> fieldDefinitionvalueList = new ArrayList<>();

        try {
            List<FieldDefinition> list = fieldDefinitonDAO.findAll();

            for (FieldDefinition f : list) {
                fieldDefinitionvalueList.add(new FieldDefinitionValue(f, ""));
            }
        } catch (Exception e) {
            logger.debug("Error loading fdid={}", e);
        }

        return fieldDefinitionvalueList;
    }

    /**
     * Needs request parameter id
     * @return
     */
    public String save() {
        try {
            int templateProjectId = Integer.parseInt(Faces.getRequestParameter("id"));

            for (FieldDefinitionValue fieldDefinitions: fieldDefinitionvalueList) {
                ProjectTemplateStrings projectTemplateStrings = new ProjectTemplateStrings();
                projectTemplateStrings.setTemplateId(templateProjectId);
                projectTemplateStrings.setFdid(fieldDefinitions.getFdid().getFdid());
                projectTemplateStrings.setValue(fieldDefinitions.getValue());

                projectTemplateStringsDAO.save(projectTemplateStrings);
            }
            return ok();
        } catch (Exception e) {
            return fail();
        }
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



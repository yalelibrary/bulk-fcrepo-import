
package edu.yale.library.ladybird.web.view.template;

import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
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
 * Updates view.
 * @see ProjectTemplateView for addition of this template
 * @see TemplateEditView for viewing values for this template
 * @see TemplatePopulateView for initial populating of values
 */
@ManagedBean
@ViewScoped
public class TemplateUpdateView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private ProjectTemplate item = new ProjectTemplateBuilder().createProjectTemplate();

    @Inject
    private ProjectTemplateDAO projectTemplateDAO;

    @Inject
    private ProjectTemplateStringsDAO projectTemplateStringsDAO;

    @Inject
    private FieldDefinitionDAO fieldDefinitonDAO;

    @PostConstruct
    public void init() {
        logger.trace("Init ProjectTemplate Metadata Update View");
        initFields();
        dao = projectTemplateDAO;
    }

    /**
     * Updates value
     * @return outcome
     */
    public String update(List<FieldDefinitionValue> fieldDefinitionvalueList) {
        try {
            List<ProjectTemplateStrings> projectTemplateStringsList = new ArrayList<>();

            int templateProjectId = Integer.parseInt(Faces.getRequestParameter("id"));

            for (FieldDefinitionValue fieldDefinitions: fieldDefinitionvalueList) {
                ProjectTemplateStrings projectTemplateStrings = projectTemplateStringsDAO
                        .findByFdidAndTemplateId(fieldDefinitions.getFdid().getFdid(), templateProjectId);
                projectTemplateStrings.setValue(fieldDefinitions.getValue());
                projectTemplateStringsList.add(projectTemplateStrings);
            }

            projectTemplateStringsDAO.saveOrUpdateList(projectTemplateStringsList);
            return ok();
        } catch (Exception e) {
            logger.debug("Error updating={}", e);
            return fail();
        }
    }

    public ProjectTemplate getItem() {
        return item;
    }

    public void setItem(ProjectTemplate item) {
        this.item = item;
    }
}



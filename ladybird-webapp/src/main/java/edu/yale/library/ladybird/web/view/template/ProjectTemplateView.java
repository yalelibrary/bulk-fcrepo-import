
package edu.yale.library.ladybird.web.view.template;

import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.entity.ProjectTemplateBuilder;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import edu.yale.library.ladybird.web.view.AbstractView;
import edu.yale.library.ladybird.web.view.AuthUtil;
import edu.yale.library.ladybird.web.view.NavigationUtil;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Used for adding a new template and viewing the list of templates.
 *
 * @see ProjectTemplateView for populating this template
 * @see TemplateEditView for viewing it after it's been creating
 * @see TemplateUpdateView for updating it's values
 */
@ManagedBean
@RequestScoped
public class ProjectTemplateView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private ProjectTemplate item = new ProjectTemplateBuilder().createProjectTemplate();

    private ProjectTemplate selectedItem;

    private List<ProjectTemplate> itemList = new ArrayList<>(); //TODO

    @Inject
    private ProjectTemplateDAO projectTemplateDAO;

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        logger.trace("Init ProjectTemplateView");
        initFields();
        dao = projectTemplateDAO;
    }

    //TODO
    public List<ProjectTemplate> getItemList() {
        try {
            int currentProjectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();
            logger.trace("Current project Id={}", currentProjectId);
            return projectTemplateDAO.findByProjectId(currentProjectId);
        } catch (Exception e) {
            logger.error("Error finding items for project.", e.getMessage());
        }
        return Collections.emptyList();
    }

    public void setItemList(List<ProjectTemplate> itemList) {
        this.itemList = itemList;
    }

    /**
     * Saves and redirects
     *
     * @return main page url
     */
    public String save() {
        try {
            assertUniqueLabel(item.getLabel());

            item.setProjectId(authUtil.getDefaultProjectForCurrentUser().getProjectId());

            item.setDate(new Date());
            item.setCreator(authUtil.getCurrentUserId());

            Integer projectTemplateId = projectTemplateDAO.save(item);
            return getRedirectWithParam(NavigationUtil.PROJECT_TEMPLATE_FORM, projectTemplateId);
        } catch (Exception e) {
            logger.error("Error saving template={}", e);
            return fail();
        }
    }

    private void assertUniqueLabel(final String label) throws Exception {
        if (projectTemplateDAO.getCountByLabel(label) != 0) {
            throw new IllegalArgumentException("Not unique project label.");
        }
    }

    /**
     * Re-directs to edit page.
     *
     * @return edit page url
     */
    public String browse() {
        int projectTemplateId = selectedItem.getTemplateId();
        return getRedirectWithParam(NavigationUtil.PROJECT_TEMPLATE_EDIT_FORM, projectTemplateId);
    }

    private String getRedirectWithParam(String page, int projectTemplateId) {
        return page + "?faces-redirect=true&project_template_id=" + projectTemplateId;
    }

    public ProjectTemplate getItem() {
        return item;
    }

    public void setItem(ProjectTemplate item) {
        this.item = item;
    }

    public ProjectTemplate getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ProjectTemplate selectedItem) {
        this.selectedItem = selectedItem;
    }
}



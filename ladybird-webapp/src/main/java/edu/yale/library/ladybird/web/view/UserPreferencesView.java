package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@SessionScoped
public class UserPreferencesView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private UserPreferences userPreferences;

    private Project defaultProject = new Project();

    @Inject
    private UserPreferencesDAO entityDAO;

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserProjectDAO userProjectDAO;

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    /**
     * Create or update default project
     * @return NavigationCase result
     */
    public String updatePreferences() {
        try {
            userPreferences = new UserPreferences(authUtil.getCurrentUserId(),getProjectId(defaultProject));
            dao.saveOrUpdateList(Collections.singletonList(userPreferences));
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Error saving user project entity,", e);
            return NavigationCase.FAIL.toString();
        }
    }

    /**
     * Match project label to id. Subject to removal.
     *
     * @param defaultProject
     * @return
     */
    private int getProjectId(final Project defaultProject) {
        final List<Project> projectList = projectDAO.findByLabel(defaultProject.getLabel());
        return projectList.get(0).getProjectId(); //TODO only one
    }


    public Project getDefaultProject() {
        return defaultProject;
    }

    /**
     * Get default project for current user.
     * @return project label or null
     */
    public Project getDefaultProjectForCurrentUser() {
        Project project = null;

        try {
            final int userId = authUtil.getCurrentUserId();

            final List<UserPreferences> userPreferencesList = entityDAO.findByUserId(userId);

            if (userPreferencesList.isEmpty()) {
                logger.debug("No default project for current user.");
                return null; //TODO
            }

            final UserPreferences userPreferences = userPreferencesList.get(0); //TODO only one
            int projectId = userPreferences.getProjectId();
            project = projectDAO.findByProjectId(projectId);
        } catch (final Exception e) {
            logger.error("Error finding default user", e.getMessage());
        }
        return project;
    }

    public boolean noAssignments() {
        List<UserProject> userProjectList = new ArrayList<>();
        try {
            userProjectList = userProjectDAO.findByUserId(authUtil.getCurrentUserId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return userProjectList.isEmpty();
    }

    public void setDefaultProject(Project defaultProject) {
        this.defaultProject = defaultProject;
    }
}
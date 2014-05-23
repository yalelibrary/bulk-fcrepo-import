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

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    //TODO test
    public String updatePreferences() {
        userPreferences = new UserPreferences(); //TODO
        userPreferences.setProjectId(getProjectId(defaultProject));
        userPreferences.setUserId(getUserId(getNetid()));

        try {
            //logger.debug("Default project={}", defaultProject.toString());
            //logger.debug("Saving entity={}", userPreferences.toString());
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
        return projectList.get(0).getProjectId(); //TODO only one item anyway
    }

    /**
     * Match net id to user id. Subject to removal.
     * @param netid
     * @return
     */
    private int getUserId(final String netid) {
        final List<User> userList = userDAO.findByUsername(netid);
        return userList.get(0).getUserId(); //TODO only one item anyway
    }

    private String getNetid() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("netid").toString();
    }

    public Project getDefaultProject() {
        return defaultProject;
    }

    //TODO test
    public Project getDefaultProjectForCurrentUser() {
        Project project = null;

        try {
            final List<UserPreferences> userPreferencesList = entityDAO.findByUserId(getUserId(getNetid()));
            UserPreferences userPreferences1 = userPreferencesList.get(0);
            int projectId = userPreferences1.getProjectId();
            project = projectDAO.findByProjectId(projectId);
        } catch (final Exception e) {
            //TODO ignore first user not in the system
            if (userDAO.findAll().size() != 0) {
                logger.error("Error finding default project for current user", e);
            }
        }
        return project;
    }

    //TODO move to userprojectview
    public boolean noAssignments() {
        List<UserProject> userProjectList = new ArrayList<>();
        try {
            userProjectList = userProjectDAO.findByUserId(getUserId(getNetid()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return userProjectList.isEmpty();
    }

    public void setDefaultProject(Project defaultProject) {
        this.defaultProject = defaultProject;
    }

}



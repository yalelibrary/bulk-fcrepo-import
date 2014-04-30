package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

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

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public void updatePreferences() {
        userPreferences = new UserPreferences(); //TODO
        userPreferences.setProjectId(getProjectId(defaultProject));
        userPreferences.setUserId(getUserId(getNetid())); //TODO get from DB or session

        try {
            //logger.debug("Default project={}", defaultProject.toString());
            //logger.debug("Saving entity={}", userPreferences.toString());
            dao.save(userPreferences);
        } catch (Exception e) {
            logger.error("Error saving entity,", e);
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


    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public Project getDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(Project defaultProject) {
        this.defaultProject = defaultProject;
    }

}



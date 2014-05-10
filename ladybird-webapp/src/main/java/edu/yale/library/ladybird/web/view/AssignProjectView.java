package edu.yale.library.ladybird.web.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.ProjectRoles;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.entity.UserProjectBuilder;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AssignProjectView extends AbstractView implements Serializable {

    private static final long serialVersionUID = 6223995917417414208L;

    private final Logger logger = LoggerFactory.getLogger(AssignProjectView.class);

    private ProjectRoles projectRole;
    private Project defaultProject = new Project();
    private User defaultUser = new User();

    @Inject
    private UserProjectDAO userProjectDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    public ProjectRoles[] getRoles() {
        return ProjectRoles.values();
    }

    public ProjectRoles getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(ProjectRoles projectRole) {
        this.projectRole = projectRole;
    }

    public String save() {
        logger.debug("Assigning project id={} with role={} for user={}",
                defaultProject.getProjectId(), projectRole.name(), defaultUser.getUserId());
        final UserProject userProject = new UserProjectBuilder().
                setProjectId(defaultProject.getProjectId()).
                setUserId(defaultUser.getUserId()).
                setRole(projectRole.name()).
                setDate(new Date(System.currentTimeMillis())).
                createUserProject();
        try {
            userProjectDAO.save(userProject);
            return getRedirectWithParam(NavigationUtil.USER_PROJECTS_PAGE, defaultProject.getProjectId());
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return getRedirect("/pages/form_submission_failed");
        }
    }

    public Project getDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(Project defaultProject) {
        this.defaultProject = defaultProject;
    }

    public User getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(User defaultUser) {
        this.defaultUser = defaultUser;
    }

    private String getRedirect(String page) {
        return page + "?faces-redirect=true";
    }

    private String getRedirectWithParam(String page, int projectId) {
        return page + "?faces-redirect=true&project_id=" + projectId;
    }

}

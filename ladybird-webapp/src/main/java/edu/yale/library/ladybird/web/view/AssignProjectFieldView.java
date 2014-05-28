package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.*;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AssignProjectFieldView extends AbstractView implements Serializable {

    private static final long serialVersionUID = 6223995917417414208L;

    private final Logger logger = LoggerFactory.getLogger(AssignProjectFieldView.class);

    private User user;

    private ProjectRoles projectRole;

    private FieldDefinition fieldDefintion;

    private Project project = new Project();

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

    @PostConstruct
    public void init() {
        initFields();
        //logger.debug("user_id={}", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("user_id"));
        //logger.debug("Project_id={}", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("project_id"));
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

        //logger.debug("user_id={}", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("user_id"));
        //logger.debug("Project_id={}", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("project_id"));

        logger.debug("Saving project field label={} with role={} for user={}", project.getLabel(),
                projectRole.name(), user.getUsername());
        final UserProjectField userProject = new  UserProjectFieldBuilder().
                setProjectId(project.getProjectId()).
                setUserId(user.getUserId()).
                setFdid(fieldDefintion.getFdid()).
                setRole(projectRole.name()).
                setDate(new Date()).
                createUserProjectField();
        try {
            userProjectFieldDAO.save(userProject);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return NavigationCase.FAIL.toString();
        }
    }

    public String assign(int userId, int projectId) {
        return getRedirectWithParam(NavigationUtil.USER_METADATA_ACCESS_PAGE, userId, projectId);
    }

    private String getRedirectWithParam(String page, int userId, int projectId) {
        return page + "?faces-redirect=true&user_id=" + userId + "&project_id=" + projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public FieldDefinition getFieldDefintion() {
        return fieldDefintion;
    }

    public void setFieldDefintion(FieldDefinition fieldDefintion) {
        this.fieldDefintion = fieldDefintion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package edu.yale.library.ladybird.web.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.ProjectRoles;
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
@SessionScoped
public class AssignProjectView extends AbstractView implements Serializable {

    private static final long serialVersionUID = 6223995917417414208L;

    private final Logger logger = LoggerFactory.getLogger(AssignProjectView.class);

    private int userId;
    private ProjectRoles projectRole;
    private Project defaultProject = new Project();

    @Inject
    private UserProjectDAO userProjectDAO;

    @PostConstruct
    public void init() {
        initFields();
        userId = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("id"));
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
        logger.debug("Saving project label={} with role={} for user={}", defaultProject.getLabel(),
                projectRole.name(), userId);
        final UserProject userProject = new  UserProjectBuilder().
                setProjectId(0).
                setUserId(userId).
                setRole(projectRole.name()).
                setDate(new Date(System.currentTimeMillis())).
                createUserProject();
        try {
            userProjectDAO.save(userProject);
            return "ok";
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return "failed";
        }
    }

    public Project getDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(Project defaultProject) {
        this.defaultProject = defaultProject;
    }
}

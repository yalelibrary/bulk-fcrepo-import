package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ProjectRoles;
import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.entity.UserProjectFieldBuilder;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AssignProjectFieldView extends AbstractView implements Serializable {

    private static final long serialVersionUID = 6223995917417414208L;

    private final Logger logger = LoggerFactory.getLogger(AssignProjectFieldView.class);

    //TODO distinguish between project role and field role?
    private ProjectRoles projectRole;

    //private FieldDefinition fieldDefintion = new FieldDefinition(); //assigned as String

    private int fieldDefintion;

    //private Project project = new Project(); //passed

    //private User user; //passed

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

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

    //TODO use a converter (for fdid)
    //TODO update if the value already exists
    public String save() {

        //int userId = getParam("user_id");
       // int projectId = getParam("project_id");

        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int userId = Integer.parseInt(params.get("userId"));
        int projectId = Integer.parseInt(params.get("projectId"));

        logger.debug("Saving project id={} with field={} with role={} for user={}", projectId, fieldDefintion,
                projectRole.name(), userId);

        final UserProjectField userProjectField = new UserProjectFieldBuilder().
                setProjectId(projectId).
                setUserId(userId).
                setFdid(fieldDefintion).
                setRole(projectRole.name()).
                setDate(new Date()).
                createUserProjectField();
        try {
            logger.debug("Saving entity={}", userProjectField);
            userProjectFieldDAO.save(userProjectField);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return NavigationCase.FAIL.toString();
        }
    }

    /**
     * Redirects
     * @return page to redirect to
     */
    public String assign() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int userId = Integer.parseInt(params.get("userId"));

        //Reads either the originating get request parameter or datatable id:
        // TODO The latter case is to make page work where there's not project_id=n in the url.
        int projectId;
        if (params.get("projectId") != null && !params.get("projectId").isEmpty()) {
            projectId = Integer.parseInt(params.get("projectId"));
        } else {
            projectId = Integer.parseInt(params.get("dataTableProjectId"));
        }
        return getRedirectWithParam(NavigationUtil.USER_METADATA_ACCESS_PAGE, userId, projectId);
    }

    private String getRedirectWithParam(String page, int userId, int projectId) {
        return page + "?faces-redirect=true&user_id=" + userId + "&project_id=" + projectId;
    }

    public int getFieldDefintion() {
        return fieldDefintion;
    }

    public void setFieldDefintion(int fieldDefintion) {
        this.fieldDefintion = fieldDefintion;
    }

    private int getParam(String s) {
        return Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(s));
    }
}

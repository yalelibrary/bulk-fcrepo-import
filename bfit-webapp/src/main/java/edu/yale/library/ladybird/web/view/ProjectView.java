package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.PermissionSet;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.ProjectBuilder;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@ManagedBean (name = "ProjectView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private Project item = new ProjectBuilder().setUrl("http://www.yale.edu").setLabel("project").setLocation("location").createProject();
    private List<Project> itemList;
    private Project selectedItem = new ProjectBuilder().createProject();

    @Inject
    private ProjectDAO entityDAO;

    @Inject
    private UserDAO userDao;

    @Inject
    private AuthUtil authUtil;

    @Inject
    private RolesDAO rolesDAO;

    @Inject
    private PermissionsDAO permissionsDAO;

    @Inject
    private RolesPermissionsDAO rolesPermissionsDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public String save() {
        //TODO tmp until linked
        final UserDAO userDao = new UserHibernateDAO();
        final List<User> userList = userDao.findByEmail(item.getCreator().getEmail());
        final int creatorId = userList.get(0).getUserId();
        item.setUserId(creatorId);

        try {
            setDefaults(item);
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Throwable e) {
            logger.error("Error saving item", e);
            return NavigationCase.FAIL.toString();
        }
    }
    //TODO replace with DAO call
    public List<String> getProjectNames() {
        final List<Project> items = getItemList();
        final  List<String> list = new ArrayList<>();
        for (Project p: items) {
            list.add(p.getLabel());
        }
        return list;
    }

    public String getProjectLabel(int projectId) {
        try {
            Project project = entityDAO.findByProjectId(projectId);
            return project.getLabel();
        } catch (Exception e) {
            logger.trace("Error finding project label={}", projectId);
            return null;
        }
    }

    public List<Project> getItemList() {
        final List<Project> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final Project item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
    }

    public void setItem(Project item) {
        this.item = item;
    }

    public Project getItem() {
        return item;
    }

    public Project getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Project selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Deprecated
    public void selectElement() {
        saveInSession(selectedItem.getProjectId());
    }

    public String selectAllUsersInProject() {
        return "users_projects.xhtml?faces-redirect=true&project_id=" + selectedItem.getProjectId();
    }

    @Deprecated
    private void saveInSession(final int projectId) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("projectId", projectId);
    }

    /**
     * see PermissionsValue change Permissions to map if feasible
     * @return whether the action has permissions. false if action not found or permissions false.
     */
    public boolean checkAddProjectPermission() {
        try {
            final User user = authUtil.getCurrentUser();
            final String userRoleStr = user.getRole();
            RolesPermissions rolesPerm = authUtil.getRolePermission(userRoleStr, PermissionSet.PROJECT_ADD);

            if (rolesPerm == null) {
                logger.error("Role permission not found for user={}", user);
                return false;
            }

            char permission = rolesPerm.getValue();
            return permission == 'y';

        } catch (Exception e) {
            logger.error("Error getting permissions", e);
        }
        return false;
    }

}



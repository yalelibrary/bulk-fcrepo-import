package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.ProjectBuilder;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
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

@ManagedBean (name = "ProjectView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private Project item = new ProjectBuilder().createProject();
    private List<Project> itemList;
    private Project selectedItem = new ProjectBuilder().createProject();

    @Inject
    private ProjectDAO entityDAO;

    @Inject
    private UserDAO userDao;

    @Inject
    private AuthUtil authUtil;

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
     * @see PermissionsValue change Permissions to map if feasible
     * @return whether the action has permissions. false if action not found or permissions false.
     */
    public boolean checkAddProjectPermission() {

        try {
            //1. Get user
            final User user = authUtil.getCurrentUser();

            // 2. Get permissions associated with this role
            final Roles roles = Roles.fromString(user.getRole());

            //3. Does the action have permissions
            final List<PermissionsValue> permissionsList = roles.getPermissions();
            for (final PermissionsValue p: permissionsList) {
                if (p.getPermissions().equals(Permissions.PROJECT_ADD)) {
                    return p.isEnabled();
                }
            }
        } catch (Exception e) {
            logger.error("Error getting permissions", e);
        }
        return false;
    }

}



package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AuthorityControlView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<AuthorityControl> itemList = new ArrayList<>();

    private AuthorityControl item = new AuthorityControl();

    @Inject
    private AuthorityControlDAO entityDAO;

    /** @see #getCurrentUser() */
    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
        itemList = dao.findAll();
    }

    public List<AuthorityControl> getItemList() {
        return itemList;
    }

    public void setItemList(List<AuthorityControl> itemList) {
        this.itemList = itemList;
    }

    public AuthorityControl getItem() {
        return item;
    }

    public void setItem(AuthorityControl item) {
        this.item = item;
    }

    public String save() {
        try {
            item.setUserId(getCurrentUser());
            item.setDate(new Date());
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Exception saving item", e);
            return NavigationCase.FAIL.toString();
        }
    }

    //TODO
    private int getCurrentUser() {
        final String userName =  FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("netid").toString();
        try {
            final List<User> userList = userDAO.findByUsername(userName);
            return userList.get(0).getUserId();
        } catch (Exception e) {
            logger.error("Error finding user", e.getMessage());
            return -1;
        }
    }

    //TODO remove
    public User getCurrentUserAsUser() {
        final String netid = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("netid").toString();
        final User user = userDAO.findByUsername(netid).get(0);
        return user;
    }


    /**
       @see #getCurrentUser() Gets current user via session netid
       @see edu.yale.library.ladybird.web.view.ProjectView#checkAddProjectPermission() for duplicate method
     * @see edu.yale.library.ladybird.auth.PermissionsValue change Permissions to map if feasible
     * @return whether the action has permissions. false if action not found or permissions false.
     *
     * TODO only project_add should have acid add perm.?
     */
    public boolean checkAddAcidPermission() {

        try {
            //1. Get user
            final User user = getCurrentUserAsUser();

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



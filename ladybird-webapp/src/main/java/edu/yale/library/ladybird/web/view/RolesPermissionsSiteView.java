package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Permissions;
import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

/**
 *
 */

@ManagedBean
@RequestScoped
public class RolesPermissionsSiteView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(RolesPermissionsSiteView.class);

    @Inject
    PermissionsDAO permissionsDAO;

    @Inject
    RolesDAO rolesDAO;

    @Inject
    RolesPermissionsDAO rolesPermissionsDAO;

    private boolean enabled = false;

    private Roles roles = new Roles();

    private List<Roles> rolesList;

    private Permissions permissions = new Permissions();

    private List<Permissions> permissionsList;

    private List<RolesPermissions> itemList;

    @PostConstruct
    public void init() {
        initFields();
        rolesList = rolesDAO.findAll();
        permissionsList = permissionsDAO.findAll();
        itemList = rolesPermissionsDAO.findAll();
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public List<Roles> getRolesList() {
        return rolesList;
    }

    public void setRolesList(List<Roles> rolesList) {
        this.rolesList = rolesList;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public List<Permissions> getPermissionsList() {
        return permissionsList;
    }

    public void setPermissionsList(List<Permissions> permissionsList) {
        this.permissionsList = permissionsList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RolesPermissions> getItemList() {
        return itemList;
    }

    public void setItemList(List<RolesPermissions> itemList) {
        this.itemList = itemList;
    }

    //todo
    public String getRoleName(int roleId) {
        return rolesDAO.findById(roleId).getRoleName();

    }

    public String getPermissionsName(int permissionsId) {
        return permissionsDAO.findById(permissionsId).getPermissionsName();

    }

    public String save() {

        logger.debug("Updating permssion={} for role={} with value={}",
                permissions.getPermissionsId(), roles.getRoleId(), enabled);
        try {
            //TODO auto conversion
            RolesPermissions rolesPermissions = new RolesPermissions();
            rolesPermissions.setRoleId(roles.getRoleId());
            rolesPermissions.setPermissiosnId(permissions.getPermissionsId());

            if (enabled) {
                rolesPermissions.setValue('y');
            } else {
                rolesPermissions.setValue('n');
            }

            RolesPermissions id = rolesPermissionsDAO.findByRolesPermissionsId(roles.getRoleId(), permissions.getPermissionsId());

            if (id == null) {
                rolesPermissionsDAO.save(rolesPermissions);
            } //else {
                //TODO update
            //}
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Error updating or saving roles permissions pair", e);
        }
        return NavigationCase.FAIL.toString();
    }
}

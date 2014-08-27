package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Helper for default/intial roles and permissions
 */
@ManagedBean
@ViewScoped
public class PermsisionsView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(PermsisionsView.class);

    private String role = "";

    private List<PermissionsValue> itemList;

    @PostConstruct
    public void init() {
        try {
            role = Faces.getRequestParameterMap().get("role");
            itemList = getPermissionsValueForRole(role);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //TODO
    private List<PermissionsValue> getPermissionsValueForRole(final String role) {
        if (role.equalsIgnoreCase("ADMIN")) {
            return Roles.ADMIN.getPermissions();
        } else if (role.equalsIgnoreCase("VISITOR")) {
            return Roles.VISITOR.getPermissions();
        } else if (role.equalsIgnoreCase("PROJECTADMIN")) {
            return Roles.PROJECT_ADMIN.getPermissions();
        } else if (role.equalsIgnoreCase("PROJECTUSER")) {
            return Roles.PROJECT_USER.getPermissions();
        }
        return Roles.NONE.getPermissions();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<PermissionsValue> getItemList() {
        return itemList;
    }

    public void setItemList(List<PermissionsValue> itemList) {
        this.itemList = itemList;
    }

}


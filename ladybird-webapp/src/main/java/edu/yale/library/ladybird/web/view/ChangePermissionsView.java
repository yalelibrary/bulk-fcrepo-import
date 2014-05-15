package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 *
 */

@ManagedBean
@SessionScoped
public class ChangePermissionsView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(ChangePermissionsView.class);

    private String role = "";
    private List<PermissionsValue> itemList;

    public ChangePermissionsView() {
    }

    @PostConstruct
    public void init() {
        try {
            role = getRoleFromRequest();
            logger.debug("Getting permissions for role={}", role);
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
        role = getRoleFromRequest();
        logger.debug("Getting permissions for role={}", role);

        itemList = getPermissionsValueForRole(role);
        logger.debug("Getting item list={}", itemList);

        return itemList;
    }

    public void setItemList(List<PermissionsValue> itemList) {
        this.itemList = itemList;
    }

    public String save() {
        final String role = getRoleFromRequest();
        logger.debug("Role is={}", role);
        logger.debug("saving list={}", itemList.toString());

        final Roles roles = Roles.fromString(role);
        roles.setPermissions(itemList);
        logger.debug("Updated permissions list in session");

        return NavigationCase.OK.toString();
    }

    private String getRoleFromRequest() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("role");
    }

}


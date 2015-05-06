package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.web.view.AbstractView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class RolesPermissionsSiteView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(RolesPermissionsSiteView.class);

    @Inject
    PermissionsDAO permissionsDAO;

    @Inject
    RolesDAO rolesDAO;

    @Inject
    RolesPermissionsDAO rolesPermissionsDAO;

    private List<RolesPermissions> itemList;

    private RolesPermissions selectedItem = new RolesPermissions();


    @PostConstruct
    public void init() {
        initFields();
        itemList = rolesPermissionsDAO.findAll();
    }

    public void updateSelectedItem() {
        logger.info("Updating role permission setting={}", selectedItem);

        try {
            int roleId = selectedItem.getRoleId();
            int permissionsId = selectedItem.getPermissionsId();
            final RolesPermissions rolesPermissions = rolesPermissionsDAO.findByRolesPermissionsId(roleId, permissionsId);

            if (isEnabled(rolesPermissions)) {
                rolesPermissions.setValue('n');
            } else {
                rolesPermissions.setValue('y');
            }

            rolesPermissions.setCreatedDate(new Date()); //relabel created date DB field
            rolesPermissionsDAO.updateItem(rolesPermissions);
            itemList = rolesPermissionsDAO.findAll(); //to refresh
        } catch (Exception e) {
            logger.error("Error updating roles permissions pair", e);
            //TODO redirect
        }
    }


    public boolean isEnabled(final RolesPermissions rolePermissions) {
        return rolePermissions.getValue().equals('y');
    }

    public String getRoleName(int roleId) {
        return rolesDAO.findById(roleId).getRoleName();
    }

    public String getPermissionsName(int permissionsId) {
        return permissionsDAO.findById(permissionsId).getPermissionsName();
    }

    public List<RolesPermissions> getItemList() {
        return itemList;
    }

    public void setItemList(List<RolesPermissions> itemList) {
        this.itemList = itemList;
    }

    public RolesPermissions getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(RolesPermissions selectedItem) {
        this.selectedItem = selectedItem;
    }
}

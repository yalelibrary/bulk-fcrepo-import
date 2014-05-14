package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@ManagedBean
@ViewScoped
public class RoleView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(RoleView.class);

    private List<Roles> itemList = new ArrayList<>();

    private Roles selectedItem;

    @PostConstruct
    public void init() {
        final Roles[] roles =  Roles.values();
        for (Roles r: roles) {
            itemList.add(r);
        }
    }

    public List<Roles> getItemList() {
        return itemList;
    }

    public void setItemList(List<Roles> itemList) {
        this.itemList = itemList;
    }

    public Roles getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Roles selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String seeRolePermissions() {
        logger.debug("Redirecting to={}", getRedirectWithParam(NavigationUtil.ROLE_ASSIGN_SITE_PERMISSIONS));
        return getRedirectWithParam(NavigationUtil.ROLE_ASSIGN_SITE_PERMISSIONS);
    }

    private String getRedirectWithParam(String page) {
        return page + "?faces-redirect=true&role=" + selectedItem.getName();
    }


}

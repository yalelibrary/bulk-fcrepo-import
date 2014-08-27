package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.web.view.NavigationUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class RoleView implements Serializable {

    private List<Roles> itemList = new ArrayList<>();

    private Roles selectedItem;

    @PostConstruct
    public void init() {
        final Roles[] roles = Roles.values();
        itemList.addAll(Arrays.asList(roles));
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
        return getRedirectWithParam(NavigationUtil.ROLE_ASSIGN_SITE_PERMISSIONS);
    }

    private String getRedirectWithParam(String page) {
        return page + "?faces-redirect=true&role=" + selectedItem.getName();
    }


}

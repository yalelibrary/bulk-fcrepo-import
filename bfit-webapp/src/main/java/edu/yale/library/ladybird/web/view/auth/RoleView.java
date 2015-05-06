package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.auth.RoleSet;
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

    private List<RoleSet> itemList = new ArrayList<>();

    private RoleSet selectedItem;

    @PostConstruct
    public void init() {
        final RoleSet[] roles = RoleSet.values();
        itemList.addAll(Arrays.asList(roles));
    }

    public List<RoleSet> getItemList() {
        return itemList;
    }

    public void setItemList(List<RoleSet> itemList) {
        this.itemList = itemList;
    }

    public RoleSet getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(RoleSet selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String seeRolePermissions() {
        return getRedirectWithParam(NavigationUtil.ROLE_ASSIGN_SITE_PERMISSIONS);
    }

    private String getRedirectWithParam(String page) {
        return page + "?faces-redirect=true&role=" + selectedItem.getName();
    }


}

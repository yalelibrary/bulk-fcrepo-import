package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.entity.Permissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.web.view.AbstractView;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;


@ManagedBean
@RequestScoped
public class PermissionsSiteView extends AbstractView {

    private Permissions item = new Permissions();

    private List<Permissions> itemList;

    @Inject
    PermissionsDAO permissionsDAO;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        initFields();
        dao = permissionsDAO;
        itemList = dao.findAll();
    }

    public Permissions getItem() {
        return item;
    }

    public void setItem(Permissions item) {
        this.item = item;
    }

    public List<Permissions> getItemList() {
        return itemList;
    }
}

package edu.yale.library.ladybird.web.view.auth;

import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.web.view.AbstractView;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
@RequestScoped
public class RoleSiteView extends AbstractView {

    private Roles item = new Roles();

    @Inject
    RolesDAO rolesDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = rolesDAO;
    }

    public Roles getItem() {
        return item;
    }

    public void setItem(Roles item) {
        this.item = item;
    }

    public List<Roles> getItemList() {
        return rolesDAO.findAll();
    }

}

package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
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
public class RoleSiteView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(PermissionsSiteView.class);

    private Roles item = new Roles();

    private List<Roles> itemList;

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
        return dao.findAll();
    }

    public void setItemList(List<Roles> itemList) {
        this.itemList = itemList;
    }

    public String save() {
        try {
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Error persisting item", e);
            return NavigationCase.FAIL.toString();
        }
    }
}

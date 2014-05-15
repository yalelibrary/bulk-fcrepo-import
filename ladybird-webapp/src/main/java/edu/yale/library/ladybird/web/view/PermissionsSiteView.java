package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Permissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
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
public class PermissionsSiteView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(PermissionsSiteView.class);

    private Permissions item = new Permissions();

    private List<Permissions> itemList;


    @Inject
    PermissionsDAO permissionsDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = permissionsDAO;
    }

    public Permissions getItem() {
        return item;
    }

    public void setItem(Permissions item) {
        this.item = item;
    }

    public List<Permissions> getItemList() {
        return dao.findAll();
    }

    public void setItemList(List<Permissions> itemList) {
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


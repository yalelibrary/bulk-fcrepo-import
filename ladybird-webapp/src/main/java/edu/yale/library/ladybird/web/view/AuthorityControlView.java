package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AuthorityControlView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<AuthorityControl> itemList = new ArrayList<>();

    private AuthorityControl item = new AuthorityControl();

    @Inject
    private AuthorityControlDAO entityDAO;

    /** @see #getCurrentUser() */
    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
        itemList = dao.findAll();
    }

    public List<AuthorityControl> getItemList() {
        return itemList;
    }

    public void setItemList(List<AuthorityControl> itemList) {
        this.itemList = itemList;
    }

    public AuthorityControl getItem() {
        return item;
    }

    public void setItem(AuthorityControl item) {
        this.item = item;
    }

    public String save() {
        try {
            item.setUserId(getCurrentUser());
            item.setDate(new Date());
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Exception saving item", e);
            return NavigationCase.FAIL.toString();
        }
    }

    //TODO
    private int getCurrentUser() {
        final String userName =  FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("netid").toString();
        try {
            final List<User> userList = userDAO.findByUsername(userName);
            return userList.get(0).getUserId();
        } catch (Exception e) {
            logger.error("Error finding user", e.getMessage());
            return -1;
        }
    }
}



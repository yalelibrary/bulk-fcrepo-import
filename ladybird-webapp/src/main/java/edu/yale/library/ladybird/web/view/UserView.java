package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.kernel.model.User;
import edu.yale.library.ladybird.kernel.model.UserBuilder;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@SessionScoped
@SuppressWarnings("unchecked")
public class UserView extends AbstractView implements Serializable {
    private final Logger logger = getLogger(this.getClass());

    private List<User> itemList;
    private User item = new UserBuilder().createUser();

    /**
     * Used for lazy loading
     */
    private LazyDataModel<User> subItemList;

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = userDAO;
        if (subItemList == null) { //FIXME
            subItemList = new UserDataModel();
        }
    }

    public List getItemList() {
        List<User> user = dao.findAll();
        return user;
    }

    //TODO replace with DAO call
    public List getUsernameList() {
        List<User> user = getItemList();
        List<String> userNameList = new ArrayList<>();
        for (User u : user) {
            userNameList.add(u.getUsername());
        }
        return userNameList;
    }

    //TODO replace with DAO call
    public List getUserEmailList() {
        List<User> user = getItemList();
        List<String> list = new ArrayList<>();
        for (User u : user) {
            list.add(u.getEmail());
        }
        return list;
    }

    public User getItem() {
        return item;
    }

    public void setItem(User item) {
        this.item = item;
    }

    public void save() {
        try {
            setDefaults(item);
            dao.save(item);
        } catch (Exception e) {
            logger.error("Error saving item", e);
        }
    }

    /**
     * Sets date defaults
     *
     * @param item
     */
    public void setDefaults(User item) {
        Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        item.setDateCreated(date);
        item.setDateEdited(date);
        item.setDateEdited(date);
        item.setDateLastused(date);
    }

    @Override
    public String toString() {
        return item.toString();
    }

    public LazyDataModel<User> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(LazyDataModel<User> subItemList) {
        this.subItemList = subItemList;
    }
}



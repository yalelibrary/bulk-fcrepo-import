package edu.yale.library.view;


import edu.yale.library.beans.User;
import edu.yale.library.beans.UserBuilder;
import edu.yale.library.dao.UserDAO;

import org.slf4j.Logger;

import java.util.List;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class UserView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<User> itemList;

    private User item = new UserBuilder().createUser();

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = userDAO;
    }

    public List getItemList() {
        List<User> user = dao.findAll();
        return user;
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

}



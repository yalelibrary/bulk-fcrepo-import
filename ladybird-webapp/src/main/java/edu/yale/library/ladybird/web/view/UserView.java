package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserBuilder;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
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
    private User selectedItem = new UserBuilder().createUser();

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
        if (subItemList == null) {
            subItemList = new UserDataModel();
        }
    }

    public List<User> getItemList() {
        List<User> user = dao.findAll();
        return user;
    }

    public List getUsernameList() {
        try {
            final List<String> list = userDAO.getUsernames();
            return list;
        } catch (RuntimeException e) {
            logger.debug("Error finding user email list");
        }
        return Collections.emptyList();
    }

    public List getUserEmailList() {
        try {
            final List<String> list = userDAO.getEmails();
            return list;
        } catch (RuntimeException e) {
            logger.debug("Error finding user email list");
        }
        return Collections.emptyList();
    }

    public User getItem() {
        return item;
    }

    public void setItem(User item) {
        this.item = item;
    }

    public String save() {
        try {
            setDefaults(item);
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error("Error saving item", e);
            return NavigationCase.FAIL.toString();
        }
    }

    /**
     * Sets date defaults
     *
     * @param item
     */
    public void setDefaults(final User item) {
        Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        item.setDateCreated(date);
        item.setDateEdited(date);
        item.setDateLastused(date);
        item.setCreatorId(getUserIdForUsername(getCurrentUserName()));
    }

    public LazyDataModel<User> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(LazyDataModel<User> subItemList) {
        this.subItemList = subItemList;
    }

    public User getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(User selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String assignProjectFieldMetadataAccess() {
        return getRedirectWithParam(NavigationUtil.USER_METADATA_ACCESS_PAGE);
    }

    public String seeUserActivity() {
        return getRedirectWithParam(NavigationUtil.USER_EVENT_PAGE);
    }

    private String getRedirectWithParam(String page) {
        return page + "?faces-redirect=true&id=" + selectedItem.getUserId();
    }

    private int getUserIdForUsername(final String username) {
        final List<User> userList = userDAO.findByUsername(username);

        //Note: for the 1st user in the system
        if (userList.size() == 0) {
            logger.debug("Returning 1st user id, since user list={}", userList.toString());
            return 1;
        }

        return userList.get(0).getUserId(); //TODO ensure unique user name
    }

    //TODO returns empty string due to IT auth test
    //TODO get current user from somewhere else
    private String getCurrentUserName() {
        try {
            final String sessionNetIdParam = "netid";
            final String username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(sessionNetIdParam).toString();
            return username;
        } catch (Exception e) {
            logger.debug("Cannot find current username");
            return "";
        }
    }

    //TODO converter
    //TODO returns empty string due to lack of http service param for creator
    public String getUserName(final int userId) {
        try {
            return userDAO.findByUserId(userId);
        } catch (Exception e) {
            logger.debug("Cannot find user name for user id={}", userId);
            return "";
        }
    }

    @Override
    public String toString() {
        return item.toString();
    }
}



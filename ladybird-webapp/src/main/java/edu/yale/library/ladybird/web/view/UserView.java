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
        if (subItemList == null) { //FIXME
            subItemList = new UserDataModel();
        }
    }

    public List<User> getItemList() {
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
        item.setDateEdited(date);
        item.setDateLastused(date);
        item.setCreatorId(getUserIdForUsername(getCurrentUserName()));
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

    public User getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(User selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String assignProjectFieldMetadataAccess() {
        return getRedirectWithParam("user_metadata_access.xhtml");
    }

    public String seeUserActivity() {
        return getRedirectWithParam("user_event.xhtml");
    }

    private String getRedirectWithParam(String page) {
        return page + "?faces-redirect=true&id=" + selectedItem.getUserId();
    }

    private int getUserIdForUsername(final String username) {
        final List<User> userList = userDAO.findByUsername(username);
        if (userList.size() == 0) { //FIXME it's for the 1st user
            logger.debug("Returning 1st user id, since user list={}", userList.toString());
            return 1;
        }
        return userList.get(0).getUserId(); //TODO only one anyway
    }

    private String getCurrentUserName() {
        try {
            final String netid = FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get("netid").toString();
            return netid;
        } catch (Exception e) {
            return ""; //TODO
        }
    }
}



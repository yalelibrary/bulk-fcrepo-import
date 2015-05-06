package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@ManagedBean
@ViewScoped
public class UserEventView extends AbstractView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(UserEventView.class);

    private int userId;

    private String user = ""; //TODO

    private List<UserEvent> itemList;

    public UserEventView() {
    }

    @Inject
    UserEventDAO entityDAO;

    @Inject
    UserDAO userDAO;

    @Inject
    AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;

        String userIdStr = Faces.getRequestParameter("id");

        if (userIdStr == null || userIdStr.isEmpty()) {
            logger.error("No user id found in param");
            itemList = Collections.emptyList();
        }

        try {
            userId = Integer.parseInt(userIdStr);
            user = convertUserIdToUserName(userId);
            itemList = entityDAO.findByUserId(user);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

    public List<UserEvent> getItemList() {
        return itemList;
    }

    public int getUserId() {
        return userId;
    }

    public String getUser() {
        return user;
    }

    private String convertUserIdToUserName(final int userId) {
        String userName = "";

        try {
            userName = userDAO.findUsernameByUserId(userId);
        } catch (Exception e) {
            logger.debug("Error", e);
        }
        return userName;
    }
}

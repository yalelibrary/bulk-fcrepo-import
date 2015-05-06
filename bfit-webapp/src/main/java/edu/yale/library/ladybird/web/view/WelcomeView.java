package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@SessionScoped
@ManagedBean
public class WelcomeView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(WelcomeView.class);

    public static final String WEB_XML_PRINCIPAL_LOGIN_IDENTIFIER = "netid-last-act-time";

    @Inject
    private AuthUtil authUtil;

    @Inject
    private UserProjectDAO userProjectDAO;

    @Inject
    private ImportJobDAO importJobDAO;

    @Inject
    private ImportJobNotificationsDAO importJobNotificationsDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    public Date getPrincipalLastActTime() {
        try {
            //or get session creation time
            return new Date((long) Faces.getSessionAttribute(WEB_XML_PRINCIPAL_LOGIN_IDENTIFIER));
        } catch (NullPointerException e) {
            logger.error("Cannot find principle last time {}", e.getMessage());
        }
        return new Date();
    }

    /**
     * Returns list of projects for current user
     *
     * @return list of projects
     */
    public List<UserProject> projectsForCurrentUser() {
        List<UserProject> userProjectList = new ArrayList<>();
        try {
            userProjectList = userProjectDAO.findByUserId(authUtil.getCurrentUserId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return userProjectList;
    }

    /**
     * Gets all import jobs for current user
     */
    public List<ImportJob> jobsForCurrentUser() {
        try {
            List<ImportJob> importJobs = importJobDAO.findByUser(authUtil.getCurrentUserId());
            logger.trace("Import jobs for current user={}", importJobs.toString());
            return importJobs;
        } catch (Exception e) {
            logger.debug("No current user or error finding jobs.");
            return new ArrayList<>();
        }
    }

    /**
     * Finds out if notifications have been sent for the current user
     * @return false if exception.
     */
    public boolean notificationSentForCurrentUser(int jobId) {
        try {
            List<ImportJobNotifications> notifications = importJobNotificationsDAO.findByUserAndJobId(authUtil.getCurrentUserId(), jobId);
            if (notifications.isEmpty()) {
                return false;
            }
            return (notifications.get(0).getNotified() == 1);
        } catch (Exception e) {
            logger.error("Error finding notifications for current user. {}", e.getMessage());
            logger.trace("Error finding notification for current user", e);
            return false;
        }
    }

    //TODO move
    public String getCurrentUsername() {
        User user;

        try {
            user = authUtil.getCurrentUser();
        } catch (Exception e) {
            return "N/A";
        }

        String name = user.getName();

        if (name != null && !name.isEmpty()) {
            return name;
        }

        String userName = user.getUsername();

        if (userName != null && !userName.isEmpty()) {
            return userName;
        }

        return "N/A";
    }

    public String getWelcomeUrl() {
        String s = ApplicationProperties.CONFIG_STATE.WELCOME_PAGE;
        if (s == null || s.isEmpty()) {
            logger.error("Empty welcome page");
            throw new IllegalStateException("Cannot proceed");
        }

        return s;
    }



}

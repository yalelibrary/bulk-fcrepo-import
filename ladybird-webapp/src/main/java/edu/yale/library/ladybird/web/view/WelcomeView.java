package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SessionScoped
@ManagedBean
public class WelcomeView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(WelcomeView.class);

    public static final String webXmlPrincipalLoginIdentifier = "netid-last-act-time";

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
            return new Date((long) getSessionAttribute(webXmlPrincipalLoginIdentifier)); //or get session creation time
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
        return new Date(System.currentTimeMillis());
    }

    @Deprecated
    private Object getSessionAttribute(final String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
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
     * @return
     */
    public List<ImportJob> jobsForCurrentUser() {
        try {
            List<ImportJob> importJobs = importJobDAO.findByUser(authUtil.getCurrentUserId());
            logger.trace("Found list={}", importJobs.toString());
            return importJobs;
        } catch (Exception e) {
            logger.debug("No current user or error finding jobs.");
            return new ArrayList<>();
        }
    }

    /**
     * Finds out if notifications have been sent for the current user
     * @return return. false if exception.
     */
    public boolean notificationSentForCurrentUser(int jobId) {
        try {
            List<ImportJobNotifications> notifications = importJobNotificationsDAO.findByUserAndJobId(authUtil.getCurrentUserId(), jobId);
            return (notifications.get(0).getNotified() == 1);
        } catch (Exception e) {
            logger.error("Error finding notifications", e);
            return false;
        }
    }

}

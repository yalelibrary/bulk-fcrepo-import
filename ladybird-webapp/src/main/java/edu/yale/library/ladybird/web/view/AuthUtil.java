package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

public class AuthUtil extends AbstractView {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserDAO userDAO;

    @Inject
    private UserPreferencesDAO entityDAO;

    @Inject
    private ProjectDAO projectDAO;

    public AuthUtil() {
        initFields();
    }

    /**
     * Get default project for current user.
     * @return project label or null
     */
    public Project getDefaultProjectForCurrentUser() {
        Project project = null;

        try {
            final int userId = getCurrentUserId();

            final List<UserPreferences> userPreferencesList = entityDAO.findByUserId(userId);

            if (userPreferencesList.isEmpty()) {
                logger.debug("No default project for current user.");
                return null; //TODO
            }

            final UserPreferences userPreferences = userPreferencesList.get(0); //TODO only one
            int projectId = userPreferences.getProjectId();
            project = projectDAO.findByProjectId(projectId);
        } catch (final Exception e) {
            logger.error("Error finding default user/project", e.getMessage());
        }
        return project;
    }

    /**
     * Match net id to user id. Subject to removal.
     *
     * @param netid
     * @return
     */
    public int getUserId(final String netid) {
        try {
            final List<User> list = userDAO.findByUsername(netid);

            if (list.isEmpty()) {
                throw new NoSuchElementException("No value for param= " + netid);
            }
            return list.get(0).getUserId(); //TODO only one
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Match net id to user id. Subject to removal.
     *
     * @return
     */
    public int getCurrentUserId() {

        final String netid = getNetid();

        try {
            final List<User> list = userDAO.findByUsername(netid);

            if (list.isEmpty()) {
                throw new NoSuchElementException("No value for param= " + netid);
            }
            return list.get(0).getUserId(); //TODO only one
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get current User
     *
     * @return User
     * @throws NoSuchElementException if no such user name exist
     */
    public User getCurrentUser() {
        final String netid = getNetid();

        final List<User> list = userDAO.findByUsername(netid);

        if (list.isEmpty()) {
            throw new NoSuchElementException("No value for param= " + netid);
        }

        User user = list.get(0);
        return user;
    }

    private String getNetid() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("netid").toString();
    }

}

package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.PermissionSet;
import edu.yale.library.ladybird.auth.RoleSet;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class AuthUtil extends AbstractView {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserDAO userDAO;

    @Inject
    private UserPreferencesDAO entityDAO;

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private RolesDAO rolesDAO;

    @Inject
    private PermissionsDAO permissionsDAO;

    @Inject
    private RolesPermissionsDAO rolesPermissionsDAO;

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
                logger.trace("No default project for current user.");
                return null; //TODO
            }

            final UserPreferences userPreferences = userPreferencesList.get(0); //TODO only one
            int projectId = userPreferences.getProjectId();
            project = projectDAO.findByProjectId(projectId);
        } catch (final Exception e) {
            logger.trace("Error finding default user/project", e.getMessage());
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
                throw new NoSuchElementException("No value for param=" +  netid);
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

    /**
     * Get current User or null
     *
     * @return User or null
     */
    public User getCurrentUserOrNull() {
        try {
            final String netid = getNetid();
            final List<User> list = userDAO.findByUsername(netid);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            logger.trace("Error finding current user", e.getMessage());
            return null;
        }
    }

    private String getNetid() {
        Object netId =  Faces.getSessionMap().get("netid");
        return (netId == null) ? "" : netId.toString();
    }

    public RolesPermissions getRolePermission(String userRoleStr, PermissionSet requiredPermission) {
        try {
            final RoleSet role = RoleSet.fromString(userRoleStr);
            final int roleId = rolesDAO.findByName(role.getName()).getRoleId();
            final int permissionsId = permissionsDAO.findByName(requiredPermission.getName()).getPermissionsId();
            return rolesPermissionsDAO.findByRolesPermissionsId(roleId, permissionsId);
        } catch (Exception e) {
            logger.error("Error getting role permission", e);
            return null; //TODO
        }
    }

}

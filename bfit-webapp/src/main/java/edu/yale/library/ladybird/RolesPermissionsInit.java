package edu.yale.library.ladybird;

import edu.yale.library.ladybird.auth.PermissionSet;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.RoleSet;
import edu.yale.library.ladybird.entity.Permissions;
import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.PermissionsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesPermissionsHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class RolesPermissionsInit {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RolesPermissionsDAO rolesPermissionsDAO = new RolesPermissionsHibernateDAO();

    private final RolesDAO rolesDAO = new RolesHibernateDAO();

    private final PermissionsDAO permissionsDAO = new PermissionsHibernateDAO();

    public void load() {
        logger.debug("Loading default init permissions"); //should be removed for dynamic state

        try {

            if (rolesDAO.count() != 0 || permissionsDAO.count() != 0
                    || rolesPermissionsDAO.count() != 0) {
                logger.info("fdid already initialized. Skipping");
                return;
            }

            final PermissionSet[] permissions = PermissionSet.values();
            final Map<PermissionSet, Integer> savedIds = new HashMap<>();
            final Date date = new Date();

            for (final PermissionSet p: permissions) {
                Permissions pe = new Permissions();
                pe.setPermissionsName(p.getName());
                int savedId = permissionsDAO.save(pe);
                savedIds.put(p, savedId);
            }


            final List<RolesPermissions> rolesPermissionsList = new ArrayList<>();

            for (final RoleSet r: RoleSet.values()) {
                Roles role = new Roles();
                role.setRoleName(r.getName());
                role.setRoleDesc("");
                int roleId;

                edu.yale.library.ladybird.entity.Roles exRole = rolesDAO.findByName(r.getName());

                if (exRole != null) {
                    roleId = exRole.getRoleId();
                } else {
                    roleId = rolesDAO.save(role);
                }

                final List<PermissionsValue> p = r.getPermissions();
                // for each of these permissions

                for (final PermissionsValue v : p) {
                    RolesPermissions rolesPermissions = new RolesPermissions();
                    rolesPermissions.setCreatedDate(date);
                    rolesPermissions.setRoleId(roleId);
                    rolesPermissions.setPermissionsId(savedIds.get(v.getPermissionSet()));
                    char enabled = 'n';

                    if (v.isEnabled()) {
                        enabled = 'y';
                    }

                    rolesPermissions.setValue(enabled);
                    rolesPermissionsList.add(rolesPermissions);
                    logger.debug("Saving role permissions={}", rolesPermissions);
                }

                rolesPermissionsDAO.saveList(rolesPermissionsList);
            }
        } catch (Exception e) {
            logger.error("Error persisting role permissions", e);
            throw e;
        }
    }
}
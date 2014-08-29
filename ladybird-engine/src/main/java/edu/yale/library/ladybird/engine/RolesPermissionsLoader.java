package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.PermissionsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesPermissionsHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RolesPermissionsLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void load() {
        //Add role permissions
        try {
            logger.debug("Loading default init permissions"); //should be removed for dynamic state
            RolesPermissionsDAO rolesPermissionsDAO = new RolesPermissionsHibernateDAO();

            Date date = new Date();
            RolesDAO rolesDAO = new RolesHibernateDAO();
            PermissionsDAO permissionsDAO = new PermissionsHibernateDAO();

            Permissions[] permissions = Permissions.values();
            Map<Permissions, Integer> savedIds = new HashMap<>();

            for (Permissions p: permissions) {
                edu.yale.library.ladybird.entity.Permissions pe = new edu.yale.library.ladybird.entity.Permissions();
                pe.setPermissionsName(p.getName());
                int savedId = permissionsDAO.save(pe);
                savedIds.put(p, savedId);
            }


            for (Roles r: Roles.values()) {

                edu.yale.library.ladybird.entity.Roles role = new edu.yale.library.ladybird.entity.Roles();
                role.setRoleName(r.getName());
                role.setRoleDesc("");
                int roleId = rolesDAO.save(role);

                List<PermissionsValue> p = r.getPermissions();
                // for each of these permissions

                for (PermissionsValue pv : p) {
                    RolesPermissions rolesPermissions = new RolesPermissions();
                    rolesPermissions.setCreatedDate(date);
                    rolesPermissions.setRoleId(roleId);
                    rolesPermissions.setPermissionsId(savedIds.get(pv.getPermissions()));
                    char enabled = 'n';

                    if (pv.isEnabled()) {
                        enabled = 'y';
                    }

                    rolesPermissions.setValue(enabled);
                    rolesPermissionsDAO.save(rolesPermissions);
                    logger.debug("Saved role permissions={}", rolesPermissions);
                }
            }
        } catch (Exception e) {
            logger.error("Error init to db role permissions");
        }
    }
}
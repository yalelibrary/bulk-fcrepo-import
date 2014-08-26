package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldDefinitionBuilder;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "FieldDefinitionView")
@RequestScoped
@SuppressWarnings("unchecked")
public class FieldDefinitionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private FieldDefinition item = new FieldDefinitionBuilder().createFieldDefinition();
    private List<FieldDefinition> itemList;

    @Inject
    private FieldDefinitionDAO entityDAO;

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public String save() {
        try {
            setDefaults(item);
            dao.save(item);
            return NavigationCase.OK.toString();
        } catch (Throwable e) {
            logger.error("Error saving item", e);
            return NavigationCase.FAIL.toString();
        }
    }

    public List<FieldDefinition> getItemList() {
        List<FieldDefinition> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final FieldDefinition item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
    }

    public FieldDefinition getItem() {
        return item;
    }

    public void setItem(FieldDefinition item) {
        this.item = item;
    }

    /**
     * @see PermissionsValue change Permissions to map if feasible
     * @return whether the action has permissions. false if action not found or permissions false.
     */
    public boolean checkAddFdidPermission() {
        try {
            final User user = authUtil.getCurrentUser();
            final String userRoleStr = user.getRole();
            RolesPermissions rolesPerm = authUtil.getRolePermission(userRoleStr, Permissions.FDID_ADD);

            if (rolesPerm == null) {
                logger.error("Role permisison not found for user={}", user);
                return false;
            }

            char permission = rolesPerm.getValue();
            return permission == 'y';

        } catch (Exception e) {
            logger.error("Error getting permissions", e);
        }
        return false;
    }
}



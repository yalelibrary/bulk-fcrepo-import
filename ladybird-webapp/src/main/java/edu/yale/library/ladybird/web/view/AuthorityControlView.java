package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unchecked")
@ManagedBean
@RequestScoped
public class AuthorityControlView extends AbstractView {

    private final Logger logger = getLogger(this.getClass());

    /**
     * Items for viewing
     */
    private List<AuthorityControl> itemList = new ArrayList<>();

    /**
     * Item for editing
     */
    private AuthorityControl item = new AuthorityControlBuilder().createAuthorityControl();

    @Inject
    private AuthorityControlDAO authControlDao;

    @Inject
    private AuthUtil authUtil;

    @Inject
    private FieldDefinitionDAO fieldDefinitionDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = authControlDao;
        Integer acid = null;
        try {
            acid = Integer.parseInt(Faces.getRequestParameter("acid"));
        } catch (NumberFormatException e) {
            logger.trace("No param", e);
        }

        try {
            if (acid != null) {
                itemList = authControlDao.findByFdid(acid);
            }
        } catch (Exception e) {
            logger.error("Error finding by fdid", e);
        }
    }

    /**
     * Returns count or 0
     *
     * @param fdid fdid
     * @return count or 0
     */
    public int countForFdid(int fdid) {
        try {
            int count = authControlDao.countByFdid(fdid);
            logger.debug("Fdid={} acid count={}", fdid, count);
            return count;
        } catch (Exception e) {
            logger.error("Error", e);
            return 0;
        }
    }

    /**
     * Get all acid values
     */
    public List<FieldDefinition> getAcids() {
        List<FieldDefinition> list = fieldDefinitionDAO.findAll();

        logger.debug("Original Fdid size={}", list.size());

        list.removeIf(p -> FieldConstantUtil.isString(p.getFdid()));

        logger.debug("New fdid size={}", list.size());
        return list;
    }

    //TODO
    public String getLabel(final FieldDefinition f) {
        return f.getFdid() + " ( " + f.getHandle() + " ) ";
    }

    /**
     * Add new acid value
     *
     * @return page to navigate to
     */
    public String save() {
        try {
            item.setUserId(authUtil.getCurrentUserId());
            item.setDate(new Date());
            dao.save(item);
            return ok();
        } catch (Exception e) {
            logger.error("Exception saving item", e);
            return fail();
        }
    }

    /**
     * see PermissionsValue change Permissions to map if feasible
     * @return whether the action has permissions. false if action not found or permissions false.
     */
    public boolean checkAddAcidPermission() {
        try {
            final User user = authUtil.getCurrentUser();
            final String userRoleStr = user.getRole();
            RolesPermissions rolesPerm = authUtil.getRolePermission(userRoleStr, Permissions.ACID_ADD);

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

    //getters setters -------------------------------------------

    public List<AuthorityControl> getItemList() {
        return itemList;
    }

    public void setItemList(List<AuthorityControl> itemList) {
        this.itemList = itemList;
    }

    public AuthorityControl getItem() {
        return item;
    }

    public void setItem(AuthorityControl item) {
        this.item = item;
    }

}



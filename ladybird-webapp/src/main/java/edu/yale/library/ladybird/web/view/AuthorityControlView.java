package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.auth.Permissions;
import edu.yale.library.ladybird.auth.PermissionsValue;
import edu.yale.library.ladybird.auth.Roles;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            return authControlDao.countByFdid(fdid);
        } catch (Exception e) {
            logger.error("Error", e);
            return 0;
        }
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
     * @return whether the action has permissions. false if action not found or permissions false.
     * TODO only project_add should have acid add perm.?
     * @see edu.yale.library.ladybird.web.view.ProjectView#checkAddProjectPermission() for duplicate method
     * @see edu.yale.library.ladybird.auth.PermissionsValue change Permissions to map if feasible
     */
    public boolean checkAddAcidPermission() {
        try {
            //1. Get user
            final User user = authUtil.getCurrentUser();

            // 2. Get permissions associated with this role
            final Roles roles = Roles.fromString(user.getRole());

            //3. Does the action have permissions
            final List<PermissionsValue> permissionsList = roles.getPermissions();
            for (final PermissionsValue p : permissionsList) {
                if (p.getPermissions().equals(Permissions.PROJECT_ADD)) {
                    return p.isEnabled();
                }
            }
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



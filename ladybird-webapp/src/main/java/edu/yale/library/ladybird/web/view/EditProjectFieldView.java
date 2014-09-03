package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.MetadataRoles;
import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
@ManagedBean
@SessionScoped
public class EditProjectFieldView extends AbstractView implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(EditProjectFieldView.class);

    /**
     * List to present and edit
     */
    private List<UserProjectField> itemList;

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    /**
     * Find by request params
     *
     * @return
     */
    private List<UserProjectField> populateItemList() {
        try {
            if (itemList == null || itemList.isEmpty()) {
                int userId = Integer.parseInt(Faces.getRequestParameter("user_id"));
                int projectId = Integer.parseInt(Faces.getRequestParameter("project_id"));

                itemList = userProjectFieldDAO.findByUserAndProject(userId, projectId);
            }
        } catch (Exception e) {
            logger.error("Param(s) null", e);
        }
        return itemList;
    }

    /**
     * Edits fields
     */
    public String edit() {
        //logger.debug("Editing entity={}", itemList.toString());
        try {
            userProjectFieldDAO.saveOrUpdateList(itemList);
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return fail();
        }
        return ok();
    }

    /**
     * Get list of UserProjectField
     *
     * @return
     */
    public List<UserProjectField> getItemList() {
        //logger.debug("Getting item list");
        if (itemList == null || itemList.isEmpty()) {
            //logger.debug("Populating item list");
            return populateItemList();
        }
        return itemList;
    }

    public void setItemList(final List<UserProjectField> itemList) {
        this.itemList = itemList;
    }

    public MetadataRoles[] getRoles() {
        return MetadataRoles.values();
    }
}


package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.metadata.ProjectTemplateApplicator;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ObjectFileView")
//@ViewScoped
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectFileView extends AbstractView {

    private final Logger logger = getLogger(this.getClass());

    private List<ObjectFile> itemList = new ArrayList<>();

    /** To be used to apply template */
    private ProjectTemplate templateItem = new ProjectTemplate();

    @Inject
    private ObjectFileDAO entityDAO;

    @Inject
    private ObjectStringDAO objectStringDAO;

    @Inject
    private ObjectAcidDAO objectAcidDAO;

    @Inject
    private ObjectDAO objectDAO;

    @Inject
    private AuthUtil authUtil;

    @Inject
    private ProjectTemplateDAO projectTemplateDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;

        try {
            if (itemList.isEmpty()) {
                //Find objects for only current project

                if (authUtil.getCurrentUserOrNull() == null) {
                    logger.info("Current user not found in session. Can't determine default project"); //should have a filter somehwere
                }

                itemList = findItems();
            }
        } catch (Exception e) {
            logger.error("Error init bean", e);
        }
    }

    private List<ObjectFile> findItems() {
        try {
            int currentProjectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();
            itemList = entityDAO.findByProject(currentProjectId); //TODO need to revisit dao method
            return itemList;
        } catch (Exception e) {
            logger.error("Error finding items", e);
            return Collections.emptyList();
        }
    }


    public List<ObjectFile> getItemList() {
        return itemList;
    }

    public void setItemList(final List<ObjectFile> itemList) {
        this.itemList = itemList;
    }

    /**
     * Applies template to all objects in the current project
     * @return navigation outcome
     */
    public String applyTemplate() {
        final ProjectTemplate templateToApply = projectTemplateDAO.findByLabel(templateItem.getLabel()); //need converter

        try {
            final int userId = authUtil.getCurrentUserId();

            logger.debug("User={} applying template={}", userId, templateToApply);

            ProjectTemplateApplicator projectTemplateApplicator = new ProjectTemplateApplicator(); //TODO
            projectTemplateApplicator.applyTemplate(templateToApply, userId);
            return ok();
        } catch (Exception e) {
            logger.error("Error applying template", e);
            return fail();
        }
    }


    public ProjectTemplate getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(ProjectTemplate templateItem) {
        this.templateItem = templateItem;
    }
}



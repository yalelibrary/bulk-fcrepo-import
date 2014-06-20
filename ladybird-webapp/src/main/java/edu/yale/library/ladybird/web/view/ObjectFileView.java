
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ObjectFileView")
@ViewScoped
@SuppressWarnings("unchecked")
public class ObjectFileView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<ObjectFile> itemList = new ArrayList<>();

    /** To be used to apply template */
    private ProjectTemplate templateItem = new ProjectTemplate();

    @Inject
    private ObjectFileDAO entityDAO;

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        logger.trace("Init ObjectFileView");
        initFields();
        dao = entityDAO;
    }

    public List<ObjectFile> getItemList() {
        try {
            if (itemList.isEmpty()) {
                //Find objects for only current project
                int currentProjectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();
                logger.trace("Current projectId={}", currentProjectId);
                itemList = entityDAO.findByProject(currentProjectId); //TODO need to revisit dao method
                logger.trace("Item list size={}", itemList.size());
            }
        } catch (Exception e) {
            logger.error("Error finding items", e);
        }
        return itemList;
    }

    public void setItemList(final List<ObjectFile> itemList) {
        this.itemList = itemList;
    }

    //TODO
    public String applyTemplate() {
        logger.debug("Applying to results template={}", templateItem.getLabel());
        return fail();
    }

    public ProjectTemplate getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(ProjectTemplate templateItem) {
        this.templateItem = templateItem;
    }
}



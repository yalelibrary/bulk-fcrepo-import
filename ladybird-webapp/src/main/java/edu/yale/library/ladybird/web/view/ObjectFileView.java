
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.metadata.ProjectTemplateApplicator;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean(name = "ObjectFileView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectFileView extends AbstractView {

    private final Logger logger = getLogger(this.getClass());

    private LazyDataModel<ObjectFile> subItemList;

    /**
     * To be used to apply template
     */
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
            if (authUtil.getCurrentUserOrNull() == null) {
                logger.info("Current user not found in session. Can't determine default project"); //should have a filter somehwere
            }

            if (subItemList == null) {
                subItemList = new ObjectFileDataModel();
            }

        } catch (Exception e) {
            logger.error("Error init bean", e);
        }
    }

    /**
     * Applies template to all objects in the current project
     *
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

    //getters and setters ---------------------------------------------------------------------------------------------

    public ProjectTemplate getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(ProjectTemplate templateItem) {
        this.templateItem = templateItem;
    }

    public LazyDataModel<ObjectFile> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(LazyDataModel<ObjectFile> subItemList) {
        this.subItemList = subItemList;
    }
}



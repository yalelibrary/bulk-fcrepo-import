package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.beans.Project;
import edu.yale.library.ladybird.kernel.beans.ProjectBuilder;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ProjectView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private Project item = new ProjectBuilder().createProject();
    private List<Project> itemList;

    @Inject
    private ProjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public void save() {
        try {
            logger.debug("Saving item", item);
            setDefaults(item);
            dao.save(item);
        } catch (Throwable e) {
            logger.error("Error saving item", e);
        }
    }

    public List<Project> getItemList() {
        List<Project> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final Project item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
    }

    public void setItem(Project item) {
        this.item = item;
    }

    public Project getItem() {
        return item;
    }
}



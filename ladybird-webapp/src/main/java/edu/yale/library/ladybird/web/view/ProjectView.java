package edu.yale.library.ladybird.web.view;

import edu.yale.library.entity.model.Project;
import edu.yale.library.entity.model.ProjectBuilder;
import edu.yale.library.entity.model.User;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
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
    private Project selectedItem = new ProjectBuilder().createProject();

    @Inject
    private ProjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public void save() {
        //TODO tmp until linked
        final UserDAO userDao = new UserHibernateDAO();
        final List<User> userList = userDao.findByEmail(item.getCreator().getEmail());
        final int creatorId = userList.get(0).getUserId();
        item.setUserId(creatorId);

        try {
            logger.debug("Saving item={}", item);
            setDefaults(item);
            dao.save(item);
        } catch (Throwable e) {
            logger.error("Error saving item", e);
        }
    }
    //TODO replace with DAO call
    public List<String> getProjectNames() {
        final List<Project> items = getItemList();
        final  List<String> list = new ArrayList<>();
        for (Project p: items) {
            list.add(p.getLabel());
        }
        return list;
    }

    public List<Project> getItemList() {
        final List<Project> list = dao.findAll();
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

    public Project getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Project selectedItem) {
        this.selectedItem = selectedItem;
    }


    public void selectElement() {
        saveInSession(selectedItem.getProjectId());
    }

    private void saveInSession(final int projectId) {
        logger.debug("Saving in session projectId={}", projectId);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("projectId", projectId);
    }
}



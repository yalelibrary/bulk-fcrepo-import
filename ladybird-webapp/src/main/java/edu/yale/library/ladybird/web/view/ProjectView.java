package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.beans.Project;
import edu.yale.library.ladybird.kernel.beans.ProjectBuilder;
import edu.yale.library.ladybird.kernel.beans.User;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
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
}



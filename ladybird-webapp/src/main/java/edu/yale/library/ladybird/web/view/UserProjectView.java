
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class UserProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<UserProject> itemList;

    private int projectId = -1;

    /* For page display */
    private String projectName;

    @Inject
    private UserProjectDAO userProjectDao;

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = userProjectDao;
        try {
            projectId = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("project_id"));
            setProjectName(projectDAO.findByProjectId(projectId).getLabel());
        } catch (RuntimeException e) {
            logger.debug("No param or error retrieving param value(s) for page.", e.getMessage());
        }
    }

    public List<UserProject> getItemList() {
        if (projectId == -1) {
            return userProjectDao.findAll();
        } else {
            final List<UserProject> list = userProjectDao.findByProjectId(projectId);
            return list;
        }
    }

    public void setItemList(List<UserProject> itemList) {
        this.itemList = itemList;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    //TODO converter
    public String getUserName(final int userId) {
        String userName = "";

        try {
            userName = userDAO.findUsernameByUserId(userId);
            if (userName == null) {
                logger.trace("User name null for userId={}. Full list={}", userId, userDAO.findAll());
            }
        } catch (Exception e) {
            logger.debug("Error", e);
        }
        return userName;
    }

    //TODO converter
    public String getProjectName(final int projectId) {
        return projectDAO.findByProjectId(projectId).getLabel();
    }

}



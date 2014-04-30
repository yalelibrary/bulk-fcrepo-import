
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class UserProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<UserProject> itemList;

    @Inject
    private UserProjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public List<UserProject> getItemList() {
        return entityDAO.findAll();
    }

    public void setItemList(List<UserProject> itemList) {
        this.itemList = itemList;
    }
}



package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.AccessconditionProject;
import edu.yale.library.ladybird.persistence.dao.AccessconditionProjectDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class AccessconditionProjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<AccessconditionProject> itemList;

    @Inject
    private AccessconditionProjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public List<AccessconditionProject> getItemList() {
        return itemList;
    }

    public void setItemList(List<AccessconditionProject> itemList) {
        this.itemList = itemList;
    }
}




package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
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

    @Inject
    private ObjectFileDAO entityDAO;

    @PostConstruct
    public void init() {
        logger.debug("Init ObjectFileView");
        initFields();
        dao = entityDAO;
    }

    public List<ObjectFile> getItemList() {
        if (itemList.isEmpty()) {
            itemList = entityDAO.findAll();
        }
        return itemList;
    }

    public void setItemList(final List<ObjectFile> itemList) {
        this.itemList = itemList;
    }
}



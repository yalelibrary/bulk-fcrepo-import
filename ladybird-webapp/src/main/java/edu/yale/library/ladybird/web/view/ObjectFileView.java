
package edu.yale.library.ladybird.web.view;


import edu.yale.library.entity.model.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ObjectFileView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectFileView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<ObjectFile> itemList;


    @Inject
    private ObjectFileDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public List<ObjectFile> getItemList() {
        return itemList;
    }

    public void setItemList(final List<ObjectFile> itemList) {
        this.itemList = itemList;
    }
}



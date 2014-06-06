package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ObjectView")
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<Object> itemList;

    @Inject
    private ObjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public List<Object> getItemList() {
        return itemList;
    }

    public void setItemList(List<Object> itemList) {
        this.itemList = itemList;
    }
}



package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
//@SessionScoped //TODO init bean was causing error for rollback
@SuppressWarnings("unchecked")
public class ObjectVersionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<ObjectVersion> itemList = new ArrayList<>();

    @Inject
    private ObjectDAO entityDAO;

    @Inject
    private ObjectVersionDAO objectVersionDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
        try {
            if (Faces.getRequestParameter("oid") != null) {
                int oidToFind = Integer.parseInt(Faces.getRequestParameter("oid"));
                itemList = objectVersionDAO.findByOid(oidToFind);
            }
        } catch (Exception e) {
            logger.error("Error finding versions.", e);
        }
    }

    public List<ObjectVersion> getItemList() {
        return itemList;
    }

    public void setItemList(List<ObjectVersion> itemList) {
        this.itemList = itemList;
    }

}



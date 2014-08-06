
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.persistence.dao.ObjectEventDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean(name = "ObjectEventView")
@RequestScoped
public class ObjectEventView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ObjectEventDAO entityDAO;

    @Inject
    private AuthUtil auth;

    private List<ObjectEvent> itemList = new ArrayList<>();

    /** For finding items by specified oid */
    private List<ObjectEvent> itemByOid = new ArrayList<>();


    int oid = 0;

    //TODO consolidate itemList and itemByOid
    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
        try {
            if (Faces.getRequestParameter("oid") == null) {
                return;
            }

            int oid = Integer.parseInt(Faces.getRequestParameter("oid"));
            int userId = auth.getCurrentUserId();

            logger.trace("Finding events for oid={} userid={}", oid, userId);
            itemList = entityDAO.findByUserAndOid(userId, oid);
        } catch (Exception e) {
            logger.error("Error init bean", e);
        }
    }

    public List<ObjectEvent> getItemList() {
        return itemList;
    }

    public void setItemList(List<ObjectEvent> itemList) {
        this.itemList = itemList;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public List<ObjectEvent> getItemByOid() {
        if (oid == 0) {
            logger.error("No user or oid");
            return new ArrayList<>();
        }

        try {
            int userId = auth.getCurrentUserId();
            itemByOid = entityDAO.findByUserAndOid(oid, userId);
        } catch (Exception e) {
            logger.error("Error finding by oid", e);
        }

        return itemByOid;
    }

    public void setItemByOid(List<ObjectEvent> itemByOid) {
        this.itemByOid = itemByOid;
    }
}



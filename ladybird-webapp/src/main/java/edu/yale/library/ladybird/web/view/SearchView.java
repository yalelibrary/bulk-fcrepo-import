package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.kernel.events.Events;
import edu.yale.library.ladybird.kernel.events.UserGeneratedEvent;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@ManagedBean
@SessionScoped
public class SearchView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(SearchView.class);

    private int oid;

    private List<Object> itemList;

    @Inject
    ObjectDAO objectDAO;

    @Inject
    UserEventDAO userEventDAO;

    @Inject
    AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public List<Object> getItemList() {
        return itemList;
    }

    public void setItemList(List<Object> itemList) {
        this.itemList = itemList;
    }

    //TODO
    public String search() {
        try {
            //Post search event
            final String netid = getCurrentUser();
            postSearchEvent(netid, oid);

            final Object o = objectDAO.findByOid(oid);
            itemList = Collections.singletonList(o);
            return NavigationCase.OK.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
            itemList = new ArrayList<>();
            return NavigationCase.OK.toString();
        }
    }

    private void postSearchEvent(final String netid, final int oid) {
        KernelBootstrap.postEvent(new UserGeneratedEvent() {
            @Override
            public String getPrincipal() {
                return netid;
            }

            @Override
            public String getValue() {
                return String.valueOf(oid);
            }

            @Override
            public String getEventName() {
                return Events.USER_SEARCH.toString();
            }
        });
    }

     private String getCurrentUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("netid").toString();
    }

    //TODO move to some other user event view?
    public List<UserEvent> getSearchEvents() {
        final String SEARCH_EVENT = Events.USER_SEARCH.name();
        List<UserEvent> list = new ArrayList<>();
        try {
            list = userEventDAO.findEventsByUser(SEARCH_EVENT, authUtil.getCurrentUser().getUsername());
        } catch (Throwable e) {
            logger.error("Error find search results", e);
        }
        return list;
    }
}

package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.beans.Collection;
import edu.yale.library.ladybird.kernel.dao.CollectionDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "CollectionView")
@RequestScoped
@SuppressWarnings("unchecked")
public class CollectionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private Collection item = new Collection();
    private List<Collection> itemList;

    @Inject
    private CollectionDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    public void save() {
        try {
            logger.debug("Saving item", item);
            setDefaults(item);
            dao.save(item);
        } catch (Throwable e) {
            logger.error("Error saving item", e);
        }
    }

    public List<Collection> getItemList() {
        List<Collection> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final Collection item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
    }

    public Collection getItem() {
        return item;
    }

    public void setItem(Collection item) {
        this.item = item;
    }
}



package edu.yale.library.ladybird.web.view;

import edu.yale.library.entity.model.FieldDefinition;
import edu.yale.library.entity.model.FieldDefinitionBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "FieldDefinitionView")
@RequestScoped
@SuppressWarnings("unchecked")
public class FieldDefinitionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private FieldDefinition item = new FieldDefinitionBuilder().createFieldDefinition();
    private List<FieldDefinition> itemList;

    @Inject
    private FieldDefinitionDAO entityDAO;

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

    public List<FieldDefinition> getItemList() {
        List<FieldDefinition> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final FieldDefinition item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
    }

    public FieldDefinition getItem() {
        return item;
    }


}



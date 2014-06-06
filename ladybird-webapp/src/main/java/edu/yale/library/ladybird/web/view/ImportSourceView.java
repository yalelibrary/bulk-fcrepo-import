
package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ImportSource;
import edu.yale.library.ladybird.entity.ImportSourceBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ImportSourceView")
@RequestScoped
public class ImportSourceView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private ImportSource item = new ImportSourceBuilder().createImportSource();
    private List<ImportSource> itemList;

    @Inject
    private ImportSourceDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

    /**
     * Saves and marks it as the active provider.
     */
    public String save() {

        item.setActive(true);
        setDefaults(item);

        //Mark old as inactive
        final List<ImportSource> list = getItemList();

        for (ImportSource importSource: list) {
            importSource.setActive(false);
        }

        list.add(item);
        dao.saveOrUpdateList(list);

        try {
            dao.saveOrUpdateList(list);
            return "ok";
        } catch (Throwable e) {
            logger.error("Error saving item", e);
        }
        return "failed";
    }

    @Deprecated
    public void setDefaults(ImportSource item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setCreatedDate(date);
    }

    public ImportSource getItem() {
        return item;
    }

    public List<ImportSource> getItemList() {
        List<ImportSource> list = dao.findAll();
        return list;
    }

    public void setItem(final ImportSource item) {
        this.item = item;
    }
}



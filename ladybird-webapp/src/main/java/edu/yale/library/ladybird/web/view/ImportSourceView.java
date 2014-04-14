
package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.beans.ImportSource;
import edu.yale.library.ladybird.kernel.beans.ImportSourceBuilder;

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

    public void save() {
        try {
            logger.debug("Saving item", item);
            setDefaults(item);
            dao.save(item);
        } catch (Throwable e) {
            logger.error("Error saving item", e);
        }
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



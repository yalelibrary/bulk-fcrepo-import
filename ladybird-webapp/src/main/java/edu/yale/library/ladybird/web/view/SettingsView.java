package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Settings;
import org.primefaces.event.CellEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@ManagedBean
@ApplicationScoped
public class SettingsView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(SettingsView.class);

    private List<Settings> itemList = new ArrayList<>();

    @Inject
    private edu.yale.library.ladybird.persistence.dao.SettingsDAO settingsDAO;

    @PostConstruct
    public void init() {
        logger.debug("Init SettingsView");
        initFields();
    }

    public List<Settings> getItemList() {
        if (itemList.isEmpty()) {
            itemList = settingsDAO.findAll();
        }
        return itemList;
    }

    public void setItemList(List<Settings> itemList) {
        this.itemList = itemList;
    }

    public void onCellEdit(CellEditEvent event) {
        logger.debug("Cell edit");
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {
                logger.debug("Old value={}, new value={} row index={}", oldValue, newValue, event.getRowIndex());


                Settings settings = settingsDAO.findById(event.getRowIndex()); //TODO check if table row id matches db row Id
                settings.setValue((String) newValue);

                settingsDAO.saveOrUpdateList(Collections.singletonList(settings));

                logger.debug("Saved={}", settingsDAO.findByProperty(settings.getProperty()).toString());

                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            logger.error("Error finding or persisting settings", e);
        }
    }
}

package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
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
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@ManagedBean
@ApplicationScoped
public class SettingsView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(SettingsView.class);

    private List<Settings> itemList = new ArrayList<>();

    @Inject
    private SettingsDAO settingsDAO;

    @PostConstruct
    public void init() {
        logger.trace("Init SettingsView");
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

    /**
       Update cell value upon edit.
       Needs revisiting
     */
    public void onCellEdit(CellEditEvent event) {
        final Object oldValue = event.getOldValue();
        final Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {

                //find where this events belongs
                List<Settings> settingsList = settingsDAO.findAll(); //NOTE, just assuming 0 for 1st row is not good, since db might be different

                if (settingsList == null || settingsList.isEmpty()) {
                    return;
                }

                Settings settings = settingsList.get(event.getRowIndex());

                //check the values match
                if (!settings.getValue().equals(oldValue)) {
                    logger.error("Won't update. False match={}", oldValue);
                    return;
                }

                logger.debug("For row index={} settings={}", event.getRowIndex(), settings);

                settings.setValue((String) newValue);

                settingsDAO.saveOrUpdateList(Collections.singletonList(settings));

                logger.debug("Saved setting={}", settingsDAO.findByProperty(settings.getProperty()).toString());

                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            logger.error("Error finding or persisting settings for row id={}", event.getRowIndex(), e);
        }
    }

    public String getSettingValueByProperty(final String key) {
        try {
            return settingsDAO.findByProperty(key).getValue();
        } catch (Exception e) {
           return "";
        }
    }
}

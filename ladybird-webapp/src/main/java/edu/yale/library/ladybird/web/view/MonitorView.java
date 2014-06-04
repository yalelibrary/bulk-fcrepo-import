package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.cron.ImportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.MonitorDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.hibernate.HibernateException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class MonitorView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<Monitor> itemList;
    private Monitor monitorItem = new Monitor();

    private UploadedFile uploadedFile;
    private String uploadedFileName;
    private InputStream uploadedFileStream;

    @Inject
    private MonitorDAO monitorDAO;

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = monitorDAO;
    }

    public String process() {
        logger.debug("Scheduling import, export jobs. Processing file={}", uploadedFileName);

        try {
            logger.debug("Saving import/export pair=" + monitorItem.toString());

            int itemId = dao.save(monitorItem);

            monitorItem.setDirPath("local");
            monitorItem.setDate(new Date());

            try {
                List<User> userList = userDAO.findByEmail(monitorItem.getNotificationEmail()); //TODO should be only 1
                monitorItem.setUser(userList.get(0));
            } catch (Exception e) {
                logger.error("Error mapping user");
                fail();
            }

            //monitorItem.getUser().setEmail(monitorItem.getNotificationEmail());

            final SpreadsheetFile file = new SpreadsheetFileBuilder()
                    .setFileName(getSessionParam("uploadedFileName").toString())
                    .setAltName(getSessionParam("uploadedFileName").toString())
                    .setFileStream((InputStream) getSessionParam("uploadedFileStream"))
                    .createSpreadsheetFile();

            final ImportRequestEvent importEvent = new ImportRequestEvent(file, monitorItem);

            ImportEngineQueue.addJob(importEvent);

            logger.debug("Enqueued event=" + importEvent.toString());

            return NavigationCase.OK.toString();
        } catch (HibernateException e) {
            logger.error("Error saving import/export job", e);
            return NavigationCase.FAIL.toString();
        }
    }

    public List getItemList() {
        final List<Monitor> monitorList;
        try {
            monitorList = dao.findAll();
            return monitorList;
        } catch (Exception e) {
            logger.error("Error finding item list={}", e);
            throw e;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            this.uploadedFileName = uploadedFile.getFileName();
            uploadedFileStream = uploadedFile.getInputstream();

            putInSession("uploadedFileName", this.uploadedFileName);
            putInSession("uploadedFileStream", this.uploadedFileStream);
        } catch (Exception e) {
            logger.error("Input stream null for file={}", event.getFile().getFileName());
        }
    }

    public Monitor getMonitorItem() {
        return monitorItem;
    }

    public void setMonitorItem(Monitor monitorItem) {
        this.monitorItem = monitorItem;
    }

    private void putInSession(String s, Object val) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(s, val);
    }

    private Object getSessionParam(String s) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(s);
    }

    @Override
    public String toString() {
        return monitorItem.toString();
    }
}



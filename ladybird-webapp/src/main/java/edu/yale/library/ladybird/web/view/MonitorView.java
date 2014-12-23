package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.cron.ImportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.MonitorDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
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

    @Inject
    private AuthUtil authUtil;

    @PostConstruct
    public void init() {
        initFields();
        dao = monitorDAO;
    }

    public String process() {
        logger.trace("Scheduling import, export jobs.");
        try {
            //validate file was uploaded
            checkNotNull(Faces.getSessionAttribute("uploadedFileName"), "No file provided for session");
            checkNotNull(Faces.getSessionAttribute("uploadedFileStream"), "No file provided for session");

            final Project currentProject = authUtil.getDefaultProjectForCurrentUser();

            checkNotNull(currentProject, "No default project for current user");

            monitorItem.setDirPath("local"); //TODO
            monitorItem.setDate(new Date());
            monitorItem.setCurrentUserId(authUtil.getCurrentUserId());
            monitorItem.setCurrentProjectId(currentProject.getProjectId());

            dao.save(monitorItem);

            logger.debug("Saved import/export pair={}", monitorItem);

            //set user id and project id
            List<User> userList = userDAO.findByEmail(monitorItem.getNotificationEmail()); //TODO should be only 1
            monitorItem.setUser(userList.get(0));
            monitorItem.setCurrentProject(currentProject);

            final Spreadsheet file = new SpreadsheetFileBuilder()
                    .filename(getSessionParam("uploadedFileName").toString())
                    .stream((InputStream) getSessionParam("uploadedFileStream"))
                    .create();

            //Queue it:
            final ImportRequestEvent importEvent = new ImportRequestEvent(file, monitorItem);
            ImportEngineQueue.addJob(importEvent);

            logger.info("Enqueued event={}", importEvent.toString());
            return ok();
        } catch (NullPointerException npe) {
            logger.error("Error={}", npe.getMessage());
            return fail();
        } catch (Exception e) { //any exception
            logger.error("Error setting up import/export job", e);
            return fail();
        } finally {
            //clear session for upload files -- otherwise it will run again with the same attributes if uploaded file is null
            Faces.removeSessionAttribute("uploadedFileName");
            Faces.removeSessionAttribute("uploadedFileStream");
        }
    }

    //TODO not sure how many times this may get hit
    public List getItemList() {
        try {
            return (userDAO.count() == 0 || authUtil.getCurrentUser() == null) ? Collections.emptyList()
                    : monitorDAO.findByUserAndProject(authUtil.getCurrentUserId(),
                            authUtil.getDefaultProjectForCurrentUser().getProjectId());
        } catch (Exception e) {
            logger.trace("Error finding monitor itemList", e);
            return Collections.emptyList();
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            this.uploadedFileName = uploadedFile.getFileName();
            uploadedFileStream = uploadedFile.getInputstream();
            putSessionAttribute("uploadedFileName", this.uploadedFileName);
            putSessionAttribute("uploadedFileStream", this.uploadedFileStream);
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

    private void putSessionAttribute(String s, Object val) {
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



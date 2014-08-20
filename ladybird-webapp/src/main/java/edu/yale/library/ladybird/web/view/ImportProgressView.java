package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.ProgressEventChangeRecorder;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO Re-design
@ManagedBean
@SessionScoped
public class ImportProgressView extends AbstractView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(ImportProgressView.class);

    @Inject
    ProgressEventChangeRecorder progressEventChangeRecorder;

    private int STEPS_TO_COMPLETE;
    private int count = 0;
    private String status = "";

    @PostConstruct
    public void init() {
        initFields();
        STEPS_TO_COMPLETE = progressEventChangeRecorder.getExpectedTotalSteps();
    }

    public int count(int jobId) {
        return progressEventChangeRecorder.getSteps(jobId);
    }

    public void progress(int jobId) {
        count = progressEventChangeRecorder.getSteps(jobId);
    }

    public String status(int jobId) {
        status = progressEventChangeRecorder.getJobStatus(jobId);
        return status;
    }

    public int getSTEPS_TO_COMPLETE() {
        return STEPS_TO_COMPLETE;
    }

    public String rawExceptionMessage() {
        if (isParamNull("id") || isParamEmpty("id")) {
            logger.debug("No param");
        }

        int id = Integer.parseInt(Faces.getRequestParameter("id"));
        Exception e = progressEventChangeRecorder.getRawException(id);

        return e.getMessage();
    }


    public List<String> rawexception() {
        List<String> list = new ArrayList<>();

        if (isParamNull("id") || isParamEmpty("id")) {
            logger.debug("No param");
        }

        int id = Integer.parseInt(Faces.getRequestParameter("id"));
        Exception e = progressEventChangeRecorder.getRawException(id);

        StackTraceElement[] ste = e.getStackTrace();

        for (StackTraceElement s : ste) {
            list.add(String.format("%n at " + s.getClassName() + "." + s.getMethodName() + ":" + s.getLineNumber()));
        }

        return list;
    }
}

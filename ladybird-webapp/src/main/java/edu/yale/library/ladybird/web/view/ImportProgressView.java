package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.ProgressEventChangeRecorder;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
        String msg = e.getMessage();

        if (msg.isEmpty()) {
            return "N/A";
        }

        try { //TODO remove
            //strip context info since it's reported in a different method
            if (e instanceof ExceptionContext) {
                String[] s = msg.split("Exception Context");
                return s[0];
            } else {
                return msg;
            }
        } catch (Exception e1) {
            return msg;
        }

    }

    public List<String> getExceptionContext() {
        if (isParamNull("id") || isParamEmpty("id")) {
            return Collections.emptyList();
        }

        final List<String> list = new ArrayList<>();

        final int id = Integer.parseInt(Faces.getRequestParameter("id"));
        final Exception e = progressEventChangeRecorder.getRawException(id);

        if (e instanceof ExceptionContext) { //TODO abstraction, format printing
            list.add(String.format("%nRow : " + ((ExceptionContext) e).getFirstContextValue("Row"))
                    + ", Column : " + ((ExceptionContext) e).getFirstContextValue("Column"));
        }

        return list;
    }


    public List<String> rawexception() {
        if (isParamNull("id") || isParamEmpty("id")) {
            return Collections.emptyList();
        }

        final List<String> list = new ArrayList<>();
        final int id = Integer.parseInt(Faces.getRequestParameter("id"));
        Exception e = progressEventChangeRecorder.getRawException(id);
        final StackTraceElement[] ste = e.getStackTrace();

        for (final StackTraceElement s : ste) {
            list.add(String.format("%n at " + s.getClassName() + "." + s.getMethodName() + ":" + s.getLineNumber()));
        }

        return list;
    }

    //gets first cause
    public List<String> getCause() {

        if (isParamNull("id") || isParamEmpty("id")) {
            return Collections.emptyList();
        }

        final List<String> list = new ArrayList<>();
        final int id = Integer.parseInt(Faces.getRequestParameter("id"));
        Exception e = progressEventChangeRecorder.getRawException(id);
        Throwable throwable = e.getCause();
        final StackTraceElement[] ste = throwable.getStackTrace();

        for (final StackTraceElement s : ste) {
            list.add(String.format("%n at " + s.getClassName() + "." + s.getMethodName() + ":" + s.getLineNumber()));
        }

        return list;
    }
}

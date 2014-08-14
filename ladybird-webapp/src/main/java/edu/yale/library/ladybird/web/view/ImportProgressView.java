package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.ProgressEventChangeRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class ImportProgressView extends AbstractView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(ImportProgressView.class);

    @Inject
    ProgressEventChangeRecorder progressEventChangeRecorder;

    private int STEPS_TO_COMPLETE;
    private int count = 0;

    @Deprecated
    private boolean complete = false;

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
        if (count == STEPS_TO_COMPLETE) {
            complete = true;
        }
    }

    public int getSTEPS_TO_COMPLETE() {
        return STEPS_TO_COMPLETE;
    }
}

package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.engine.cron.JobTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class ImportProgressView implements Serializable {

    private Logger logger = LoggerFactory.getLogger(ImportProgressView.class);

    public final int expectedCount = 2;

    private int count = 0;

    boolean complete = false;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment() {
        count = JobTracker.getSteps();
        if (count == expectedCount) {
            complete = true;
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getExpectedCount() {
        return expectedCount;
    }
}

package edu.yale.library.ladybird.engine;

import com.google.common.eventbus.Subscribe;
import edu.yale.library.ladybird.engine.cron.ProgressEvent;
import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressEventListener implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ProgressEventListener.class);

    public static final int TOTAL_STEPS = 3;
    private static final Map<Integer, Integer> steps = new HashMap<>();

    private static final Map<Integer, String> status = new HashMap<>();

    private static final Map<Integer, List<ContextedRuntimeException>> exceptions = new HashMap<>();

    /**
     * Records steps
     *
     * @param event an ExportProgressEvent
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEventCompletion(ProgressEvent event) {
        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            final int importId = event.getJobId();
            final JobStatus jobStatus = event.getEventStatus();

            // skip steps increment if not complete
            if (steps.get(event.getJobId()) != null && jobStatus == JobStatus.COMPLETE) {
                logger.trace("Incrementing step for event={} for jobId={}", event.toString(), event.getJobId());
                int current = steps.get(event.getJobId());
                steps.put(event.getJobId(), current + 1);
            } else  if (jobStatus == JobStatus.COMPLETE){
                logger.trace("Incrementing step for event={} for jobId={}", event.toString(), event.getJobId());
                steps.put(event.getJobId(), 1);
            }

            logger.trace("Recording status event={}", event.getEventName() + jobStatus.toString());
            status.put(importId, event.getEvent().getEventName() + " : " + jobStatus.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }

    /**
     * Records statuses and exceptions
     * Operates on status and exceptions
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onException(JobExceptionEvent event) {
        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            int importId = event.getJobId();
            logger.trace("Recording exception event for importId={}", importId);
            status.put(importId, JobStatus.EXCEPTION.toString());
            List<ContextedRuntimeException> list = exceptions.get(importId);

            if (list == null || list.isEmpty()) {
                list = new ArrayList<>();
            }

            list.add(event.getException());
            exceptions.put(importId, list);
        } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }

    /**
     * Called from pages
     *
     * @param jobId jobId
     * @return number of steps complete
     */
    public int getSteps(int jobId) {
        int progress;
        try {
            progress = steps.get(jobId);
        } catch (Exception e) {
            progress = 0;
        }
        return progress;
    }

    public boolean jobInMap(int jobId) {
        return steps.get(jobId) != null;
    }

    //Note: jobId is importId not montiorId
    public String getJobStatus(int jobId) {
        if (status.get(jobId) == null) {
            return "N/A";
        }

        return status.get(jobId);
    }

    public List<ContextedRuntimeException> getRawException(int jobId) {
        if (exceptions.get(jobId) == null) {
            /*
            logger.trace("Nothing for this jobId. Existing entries are:");
            Set<Integer> s = exceptions.keySet();
            for (int i: s) {
                logger.trace("Key={}", i);
            } */
            return null;
        }

        return exceptions.get(jobId);
    }

    public int getExpectedTotalSteps() {
        return TOTAL_STEPS;
    }

    public enum JobStatus {
        COMPLETE, HANGING, EXCEPTION, IN_PROGRESS
    }
}

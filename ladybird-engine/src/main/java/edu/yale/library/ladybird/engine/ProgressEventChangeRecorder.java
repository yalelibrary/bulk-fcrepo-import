package edu.yale.library.ladybird.engine;

import com.google.common.eventbus.Subscribe;
import edu.yale.library.ladybird.engine.cron.ExportProgressEvent;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressEventChangeRecorder implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ProgressEventChangeRecorder.class);

    public static final int TOTAL_STEPS = 3;
    private static final Map<Integer, Integer> steps = new HashMap<>();

    private static final Map<Integer, JobStatus> status = new HashMap<>();

    private static final Map<Integer, List<ContextedRuntimeException>> exceptions = new HashMap<>();

    /**
     * Records steps
     *
     * @param event an ExportProgressEvent
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void recordEvent(ExportProgressEvent event) {
        logger.trace("Recording event={} for jobId={}", event.toString(), event.getJobId());

        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            if (steps.get(event.getJobId()) != null) {
                int current = steps.get(event.getJobId());
                steps.put(event.getJobId(), current + 1);
            } else {
                steps.put(event.getJobId(), 1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }

    /**
     * Records progress status
     * Operates on status
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void recordEvent(ImportRequestEvent event) {
        try {
            if (event.getMonitor().getId() == null) {
                throw new NullPointerException("Job Id null");
            }

            int importId = event.getMonitor().getId();
            logger.trace("Recording in progress event for importId={}", importId);
            status.put(importId, JobStatus.IN_PROGRESS);
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
    public void recordEvent(JobExceptionEvent event) {
        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            int importId = event.getJobId();
            logger.trace("Recording exception event for importId={}", importId);
            status.put(importId, JobStatus.EXCEPTION);
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

        return status.get(jobId).toString();
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

    private enum JobStatus {
        COMPLETE, HANGING, EXCEPTION, IN_PROGRESS;

        private String name;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }
    }
}

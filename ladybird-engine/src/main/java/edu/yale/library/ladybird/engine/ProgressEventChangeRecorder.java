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
import java.util.Set;

public class ProgressEventChangeRecorder implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ProgressEventChangeRecorder.class);

    //TODO static map
    private static final Map<Integer, Integer> progressMap = new HashMap<>();

    private static final Map<Integer, JobStatus> jobStatusMap = new HashMap<>();

    private static final Map<Integer, List<ContextedRuntimeException>> exceptionMap = new HashMap<>();

    //TODO number of steps
    public static final int expectedSteps = 2;

    @SuppressWarnings("unused")
    @Subscribe
    public void recordEvent(ExportProgressEvent event) {
        logger.trace("Recording event={} for jobId={}", event.toString(), event.getJobId());

        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            if (progressMap.get(event.getJobId()) != null) {
                int original = progressMap.get(event.getJobId());
                progressMap.put(event.getJobId(), original + 1);
            } else {
                progressMap.put(event.getJobId(), 1); //TODO
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }

    /**
     * Records progress status
     * Operates on jobStatusMap
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void recordEvent(ImportRequestEvent event) {
        try {
            if (event.getMonitor().getId() == null) {
                throw new NullPointerException("Job Id null");
            }

            int importId = event.getMonitor().getId();

            logger.debug("Recording in progress event for importId={}", importId);

            jobStatusMap.put(importId, JobStatus.IN_PROGRESS);
         } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }

    /**
     * Records statuses and exceptions
     * Operates on jobStatusMap and exceptionMap
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void recordEvent(JobExceptionEvent event) {
        try {
            if (event.getJobId() == null) {
                throw new NullPointerException("Job Id null");
            }

            int importId = event.getJobId();

            logger.trace("Recording exception event for importId={}", importId);

            jobStatusMap.put(importId, JobStatus.EXCEPTION);
            //Update exceptions for this job:
            List<ContextedRuntimeException> list = exceptionMap.get(importId);

            if (list == null || list.isEmpty()) {
                list = new ArrayList<>();
            }

            list.add(event.getException());
            exceptionMap.put(importId, list);
        } catch (Exception e) {
            throw new RuntimeException("Error saving event={}" + event.toString(), e);
        }
    }


    public int getSteps(int jobId) {
        int progress;
        try {
            progress = progressMap.get(jobId);
        } catch (Exception e) {
            progress = 0;
        }
        return progress;
    }

    public boolean jobInMap(int jobId){
        return progressMap.get(jobId) != null;
    }

    //Note: jobId is importId not montiorId
    public String getJobStatus(int jobId) {
        if (jobStatusMap.get(jobId) == null) {
            return "N/A";
        }

        return jobStatusMap.get(jobId).toString();
    }

    public List<ContextedRuntimeException> getRawException(int jobId) {
        if (exceptionMap.get(jobId) == null) {
            logger.trace("Nothing for this jobId. Existing entries are:");
            Set<Integer> s = exceptionMap.keySet();
            for (int i: s) {
                logger.trace("Key={}", i);
            }
            return null;
        }

        return exceptionMap.get(jobId);
    }

    public int getExpectedTotalSteps() {
        return expectedSteps;
    }

    enum JobStatus {
        COMPLETE, HANGING, EXCEPTION, IN_PROGRESS;

        String name;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }
    }
}

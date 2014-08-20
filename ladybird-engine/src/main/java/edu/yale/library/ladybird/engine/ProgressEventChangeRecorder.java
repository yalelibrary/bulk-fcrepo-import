package edu.yale.library.ladybird.engine;

import com.google.common.eventbus.Subscribe;
import edu.yale.library.ladybird.engine.cron.ExportProgressEvent;
import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProgressEventChangeRecorder {
    private static final Logger logger = LoggerFactory.getLogger(ProgressEventChangeRecorder.class);

    //TODO static map
    private static final Map<Integer, Integer> progressMap = new HashMap<>();

    private static final Map<Integer, JobStatus> jobStatusMap = new HashMap<>();

    private static final Map<Integer, Exception> exceptionMap = new HashMap<>();

    //TODO number of steps
    public static final int expectedSteps = 2;

    @Subscribe
    public void recordEvent(ExportProgressEvent event) {
        logger.debug("Recording event={} for jobId={}", event.toString(), event.getJobId());
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
            throw new RuntimeException("Error saving event=" + event.toString(), e);
        }
    }

    /**
     * Records statuses and exceptions
     */
    @Subscribe
    public void recordEvent(JobExceptionEvent event) {
        logger.trace("Recording job exception event={}", event.toString());
        int eventId;
        try {

            if (event.getMonitor().getId() == null) {
                throw new NullPointerException("Job Id null");
            }

            eventId = event.getMonitor().getId();

            jobStatusMap.put(eventId, JobStatus.EXCEPTION);
            exceptionMap.put(eventId, event.getException());
        } catch (Exception e) {
            throw new RuntimeException("Error saving event=" + event.toString(), e);
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

    public String getJobStatus(int jobId) {
        if (jobStatusMap.get(jobId) == null) {
            Set<Integer> keys = jobStatusMap.keySet();
            return "N/A";
        }

        return jobStatusMap.get(jobId).toString();
    }

    public Exception getRawException(int jobId) {
        if (exceptionMap.get(jobId) == null) {
            logger.debug("No exception for job={}", jobId);
            return null;
        }

        return exceptionMap.get(jobId);
    }

    public int getExpectedTotalSteps() {
        return expectedSteps;
    }

    enum JobStatus {
        COMPLETE, HANGING, EXCEPTION;

        String name;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }
    }
}

package edu.yale.library.ladybird.engine;

import com.google.common.eventbus.Subscribe;
import edu.yale.library.ladybird.engine.cron.ExportProgressEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProgressEventChangeRecorder {
    private static final Logger logger = LoggerFactory.getLogger(ProgressEventChangeRecorder.class);

    //TODO static map
    private static final Map<Integer, Integer> progressMap = new HashMap<>();

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

    public int getSteps(int jobId) {
        int progress;
        try {
            progress = progressMap.get(jobId);
        } catch (Exception e) {
            progress = 0;
        }
        return progress;
    }

    public int getExpectedTotalSteps() {
        return expectedSteps;
    }
}

package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

//replaces ExportEngineQ
public class ImportContextQueue {

    private static Logger logger = LoggerFactory.getLogger(ExportWriterQueue.class);

    static Queue<ExportRequestEvent> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ExportRequestEvent event) {
        logger.debug("Adding to queue job={}", event.toString());
        return jobQueue.add(event);
    }

    public static ExportRequestEvent getJob() {
        ExportRequestEvent event = jobQueue.poll();
        return event;
    }
}

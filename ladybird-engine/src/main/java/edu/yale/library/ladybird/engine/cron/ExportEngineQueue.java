package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue
 */
public class ExportEngineQueue {
    private static Logger logger = LoggerFactory.getLogger(ExportEngineQueue.class);
    static Queue<ExportRequestEvent> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ExportRequestEvent event) {
        logger.debug("Adding to queue job={}", event.toString());
        KernelBootstrap.postEvent(event);
        return jobQueue.add(event);
    }

    public static ExportRequestEvent getJob() {
        ExportRequestEvent event = jobQueue.poll();
        logger.debug("Polling queue job={}",event.toString());
        return event;
    }

}

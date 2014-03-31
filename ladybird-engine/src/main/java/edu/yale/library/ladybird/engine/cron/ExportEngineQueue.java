package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue
 */
public class ExportEngineQueue {
    static Queue<ExportRequestEvent> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ExportRequestEvent event) {
        return jobQueue.add(event);
    }

    public static ExportRequestEvent getJob() {
        return jobQueue.poll();
    }

}

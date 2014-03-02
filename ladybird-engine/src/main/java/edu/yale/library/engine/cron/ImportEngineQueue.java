package edu.yale.library.engine.cron;

import edu.yale.library.engine.imports.ImportRequestEvent;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue
 */
public class ImportEngineQueue {
    static Queue<ImportRequestEvent> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ImportRequestEvent event) {
        return jobQueue.add(event);
    }

    public static ImportRequestEvent getJob() {
        return jobQueue.poll();
    }

}

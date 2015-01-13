package edu.yale.library.ladybird.engine.cron.queue;

import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;

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

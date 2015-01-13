package edu.yale.library.ladybird.engine.cron.queue;

import edu.yale.library.ladybird.engine.imports.ImportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ExportWriterQueue {

    private static Logger logger = LoggerFactory.getLogger(ExportWriterQueue.class);

    static Queue<ImportContext> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ImportContext event) {
        logger.debug("Adding to queue job={}", event.toString());
        return jobQueue.add(event);
    }

    public static ImportContext getJob() {
        ImportContext event = jobQueue.poll();
        return event;
    }
}

package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.imports.ImageConversionRequestEvent;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * TODO temp. A memory queue
 */
public class ImportImageConversionQueue {

    private static Logger logger = getLogger(ImportImageConversionQueue.class);

    static Queue<ImageConversionRequestEvent> jobQueue = new ArrayBlockingQueue<>(50);

    public static boolean addJob(ImageConversionRequestEvent event) {
        logger.debug("Adding to image conversion job={}", event.toString());
        KernelBootstrap.postEvent(event);
        return jobQueue.add(event);
    }

    public static ImageConversionRequestEvent getJob() {
        ImageConversionRequestEvent event = jobQueue.poll();
        logger.debug("Got queue job={}", event.toString());
        return event;
    }

}

package edu.yale.library.engine.cron;

import edu.yale.library.engine.imports.SpreadsheetFile;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue
 */
public class ImportSpreadSheetQueue
{
    static Queue<SpreadsheetFile> jobQueue = new ArrayBlockingQueue(50);

    public static boolean addJob(SpreadsheetFile job)
    {
        return jobQueue.add(job);
    }

    public static SpreadsheetFile getJob() {
        return jobQueue.poll();
    }

}

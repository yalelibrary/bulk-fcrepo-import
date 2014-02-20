package edu.yale.library.engine.cron;


import edu.yale.library.engine.imports.SpreadsheetFile;
import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Picks file from filesystem and puts a Spreadsheet on queue
 */
public class FilePickerJob implements Job
{

    private final Logger logger = getLogger(this.getClass());

    public void monitorDirectory(final String FILE_SYS_RESOURCE)
    {
        FileSystem fs = FileSystems.getDefault();
        WatchService watchService;
        try
        {
            watchService = fs.newWatchService();
            Path path = fs.getPath(FILE_SYS_RESOURCE);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_DELETE);

            while (true)
            {
                WatchKey watchKey = watchService.take();
                List<WatchEvent<?>> watchEventList = watchKey.pollEvents();

                for (final WatchEvent watchEvent : watchEventList)
                {
                    final WatchEvent.Kind watchEventKind = watchEvent.kind();

                    if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY ||
                            watchEventKind == StandardWatchEventKinds.ENTRY_DELETE)
                    {
                        logger.debug("File modification event detected. A file was modified {} ",
                                watchEvent.context().toString());
                    }
                    if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE)
                    {
                        if (false == watchEvent.context().toString().contains("xlsx")) { //todo filter
                            continue;
                        }

                        logger.debug("Discovered a newly created spreadsheet file {} on path {} ",
                                watchEvent.context().toString(), path.toAbsolutePath());

                        /* Enqueue the new spreadsheet */

                        File fullPath = new File (path.toAbsolutePath() + System.getProperty("file.separator")
                                + watchEvent.context().toString());

                        final SpreadsheetFile file = new SpreadsheetFile(watchEvent.context().toString(),
                                path.toAbsolutePath().toString(),
                                "ImportJob-X-" + watchEvent.context().toString(),
                                FileUtils.openInputStream(fullPath));

                        logger.debug("Prepared file=" + file.toString());

                        ImportSpreadSheetQueue.addJob(file); //todo chk exception is thrown

                        logger.debug("Enqueued file=" + file.toString());
                    }
                }
                if (!watchKey.reset())
                    break;

            }
        } catch (Exception e)
        {
            logger.error("Exception monitoring filesystem resources", e);
            e.printStackTrace(); //TODO
        }
    }

    public void execute(JobExecutionContext ctx) throws JobExecutionException
    {
        logger.debug("[start] File monitoring job. Dir= " + ctx.getJobDetail().getJobDataMap().getString("path"));

        final long startTime = System.currentTimeMillis();
        monitorDirectory(ctx.getJobDetail().getJobDataMap().getString("path"));

        logger.debug("[end] File monitoring job in " + String.valueOf(System.currentTimeMillis() - startTime));
    }




}


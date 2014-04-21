package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.imports.SpreadsheetFileBuilder;
import edu.yale.library.ladybird.kernel.beans.Monitor;
import edu.yale.library.ladybird.engine.imports.ImportRequestEvent;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Picks file from filesystem and puts an ImportEvent on queue
 *
 * @see edu.yale.library.ladybird.engine.imports.ImportRequestEvent
 */
public class FilePickerJob implements Job {

    private final Logger logger = getLogger(this.getClass());
    private static final String XLSX = "xlsx";

    public void monitorDirectory(final String FILE_SYS_RESOURCE, final Monitor monitorItem) {
        final FileSystem fs = FileSystems.getDefault();
        final WatchService watchService;
        try {
            watchService = fs.newWatchService();
            final Path path = fs.getPath(FILE_SYS_RESOURCE);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                final WatchKey watchKey = watchService.take();
                final List<WatchEvent<?>> watchEventList = watchKey.pollEvents();

                for (final WatchEvent watchEvent : watchEventList) {
                    final WatchEvent.Kind watchEventKind = watchEvent.kind();

                    if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY
                            || watchEventKind == StandardWatchEventKinds.ENTRY_DELETE) {
                        logger.debug("File modification event detected. A file was modified (on folder) {} ",
                                watchEvent.context().toString());
                    }
                    if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (false == watchEvent.context().toString().contains(XLSX)) {  //todo filter
                            continue;
                        }

                        logger.debug("Discovered a newly created spreadsheet file {} on path {} ",
                                watchEvent.context().toString(), path.toAbsolutePath());

                        /* Enqueue the new spreadsheet */

                        final File fullPath = new File(path.toAbsolutePath() + System.getProperty("file.separator")
                                + watchEvent.context().toString());
                        final SpreadsheetFile file = new SpreadsheetFileBuilder()
                                .setFileName(watchEvent.context().toString()).setAltName(path.toAbsolutePath().toString())
                                .setPath("DefaultImportJob-X-" + watchEvent.context().toString())
                                .setFileStream(FileUtils.openInputStream(fullPath)).createSpreadsheetFile();
                        final ImportRequestEvent importEvent = new ImportRequestEvent(file, monitorItem);

                        logger.debug("Prepared event=" + importEvent.toString());

                        ImportEngineQueue.addJob(importEvent);

                        logger.debug("Enqueued event=" + importEvent.toString());
                    }
                }
                if (!watchKey.reset()) {
                    break;
                }

            }
        } catch (Exception e) {
            logger.error("Exception monitoring filesystem resource(s)", e);
            e.printStackTrace(); //TODO restart the job
        }
    }

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        logger.debug("FilePickerJob init");

        final Monitor monitorItem = (Monitor) ctx.getJobDetail().getJobDataMap().get("event");
        final String dir = monitorItem.getDirPath();

        logger.debug("[start] File monitoring job. Dir={} for MonitorItem={}", dir, monitorItem);

        final long startTime = System.currentTimeMillis();
        monitorDirectory(dir, monitorItem);

        logger.debug("[end] File monitoring job in " + String.valueOf(System.currentTimeMillis() - startTime));
    }

}


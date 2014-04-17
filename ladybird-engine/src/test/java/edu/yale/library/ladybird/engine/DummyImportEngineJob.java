package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.cron.ExportJob;
import edu.yale.library.ladybird.engine.cron.ImportJob;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyImportEngineJob implements Job, ImportJob, ExportJob {

    private final Logger logger = LoggerFactory.getLogger(DummyImportEngineJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final Email email = new SimpleEmail();
        try {
            email.setHostName("localhost");
            email.setSmtpPort(ImportExportSchedulerTest.PORT);
            email.setFrom("test@test.edu");
            email.setSubject("Test");
            email.setMsg("Test Message");
            email.addTo("test@test.edu");
            email.send();
        } catch (EmailException e) {
            logger.error("Exception sending notification");
            throw new JobExecutionException(e);
        }
    }
}

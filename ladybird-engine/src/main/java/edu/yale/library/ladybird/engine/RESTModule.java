package edu.yale.library.ladybird.engine;


import com.google.inject.Binder;
import com.google.inject.Module;
import edu.yale.library.ladybird.engine.http.ImportHttpService;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobsList;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobs;

public class RESTModule implements Module {

    public void configure(Binder binder) {
        binder.bind(ImportHttpService.class);
        binder.bind(ScheduledJobs.class).to(ScheduledJobsList.class);
    }

}

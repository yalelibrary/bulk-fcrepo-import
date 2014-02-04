package edu.yale.library.engine;


import com.google.inject.Module;
import com.google.inject.Binder;

import edu.yale.library.engine.cron.DefaultJobsManager;
import edu.yale.library.engine.cron.JobsManager;
import edu.yale.library.engine.http.ImportHttpService;

public class RESTModule implements Module
{

    public void configure(Binder binder)
    {
        binder.bind(ImportHttpService.class);
        binder.bind(JobsManager.class).to(DefaultJobsManager.class);
    }

}

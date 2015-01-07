package edu.yale.library.ladybird.engine.http;


import edu.yale.library.ladybird.kernel.cron.ScheduledJobs;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Http interface to manage jobs
 */

@Path("/{path: .*}/cronjobs")
public class ImportHttpService {
    private final Logger logger = getLogger(this.getClass());

    ScheduledJobs scheduledJobs;

    @Inject
    public ImportHttpService(ScheduledJobs scheduledJobs) {
        this.scheduledJobs = scheduledJobs;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getScheduledJobs() {
        return Response.ok(getAllJobs()).build();
    }

    public String getAllJobs() {

        if (scheduledJobs == null) {
            logger.error("Jobs Manager null");
        }

        return scheduledJobs.getJobs().toString();
    }
}
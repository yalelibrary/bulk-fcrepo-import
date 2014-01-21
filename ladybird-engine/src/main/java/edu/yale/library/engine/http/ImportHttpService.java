package edu.yale.library.engine.http;



import edu.yale.library.engine.JobsManager;
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
public class ImportHttpService
{
    private final Logger logger = getLogger(this.getClass());

    JobsManager jobsManager;

    @Inject
    public ImportHttpService(JobsManager jobsManager)
    {
        this.jobsManager = jobsManager;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getScheduledJobs()
    {
        return Response.ok(getAllJobs()).build();
    }

    public String getAllJobs() {

        if (jobsManager == null)
        {
            logger.error("Jobs Manager null");
        }

        return jobsManager.getJobs().toString();
    }
}
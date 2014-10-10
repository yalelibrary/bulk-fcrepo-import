package edu.yale.library.ladybird.web.http;

import edu.yale.library.ladybird.entity.CronBean;
import edu.yale.library.ladybird.web.view.AbstractView;
import edu.yale.library.ladybird.web.view.CronSchedulerView;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/{path: .*}/cron")
public class CronScheduleHttpService {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private CronSchedulerView cronSchedulerView;

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAllItems() {
        return Response.ok("noop").build(); //TODO
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(@FormParam("frequency") String importCron) {
        //TODO abstract scheduling functionailty out of CronView
        try {
            CronBean cronBean = new CronBean();

            String cron = "";

            if (importCron.equals("10")) { //TODO
                cron = "0/10 * * * * ?";
            }

            cronBean.setImportCronExpression(cron);
            cronBean.setExportCronExpression(cron);
            cronBean.setFilePickerCronExpression(cron);
            cronBean.setImageConversionExpression(cron);
            String s = cronSchedulerView.save();
            AbstractView abstractView = new AbstractView();
            if (s.equals(abstractView.ok())) {
                return Response.ok("Saved crons").build();
            } else {
                return Response.notModified("Unable to save entry").build();
            }
        } catch (Exception e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }


}



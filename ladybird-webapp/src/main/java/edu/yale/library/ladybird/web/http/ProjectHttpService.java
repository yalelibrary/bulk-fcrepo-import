package edu.yale.library.ladybird.web.http;

import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/{path: .*}/projects")
public class ProjectHttpService {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ProjectDAO dao;

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAllItems() {
        return Response.ok(getAll()).build();
    }

    public String getAll() {
        return dao.findAll().toString();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(@QueryParam("label") String label,
        @QueryParam("url") String url,
        @QueryParam("address") String address) {
        final Project item = new Project();
        item.setLabel(label);
        item.setUrl(url);
        item.setAdd1(address);

        try {
            dao.save(item);
            return Response.ok("Saved entry= " + item).build();
        } catch (Exception e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }


}


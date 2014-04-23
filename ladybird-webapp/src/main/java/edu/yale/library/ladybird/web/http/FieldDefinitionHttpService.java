package edu.yale.library.ladybird.web.http;

import edu.yale.library.ladybird.kernel.model.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/{path: .*}/fielddefinition")
public class FieldDefinitionHttpService {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private FieldDefinitionDAO dao;

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
    public Response save(@QueryParam("fdid") int fdid,
        @QueryParam("handle") String handle) {
        final FieldDefinition item = new FieldDefinition(fdid, handle);
        try {
            dao.save(item);
            return Response.ok("Saved entry= " + item).build();
        } catch (Exception e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }


}



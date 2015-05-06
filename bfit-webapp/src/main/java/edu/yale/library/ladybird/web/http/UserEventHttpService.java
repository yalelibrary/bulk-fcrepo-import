package edu.yale.library.ladybird.web.http;


import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Http interface to manage users
 *
 * @author Osman Din
 */
@Path("/{path: .*}/userevents")
public class UserEventHttpService {
    private final Logger logger = getLogger(this.getClass());

    UserEventDAO dao;

    @Inject
    public UserEventHttpService(UserEventDAO dao) {
        this.dao = dao;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAllItems() {
        return Response.ok(getAll()).build();
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    public Response deleteAll() {
        try {
            logger.debug("Removing all user events.");
            final List<UserEvent> userList = dao.findAll();
            dao.delete(userList);
            return Response.ok("Deleted all entries.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    private String getAll() {
        return dao.findAll().toString();
    }
}
package edu.yale.library.ladybird.web.http;


import edu.yale.library.ladybird.kernel.beans.User;
import edu.yale.library.ladybird.kernel.beans.UserBuilder;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.Date;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Http interface to manage usrs
 */
@Path("/{path: .*}/users")
public class UserHttpService {
    private final Logger logger = getLogger(this.getClass());

    UserDAO userDAO;

    @Inject
    public UserHttpService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAllItems() {
        return Response.ok(getAll()).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(@QueryParam("username") String userName,
                         @QueryParam("name") String name,
                         @QueryParam("email") String email) {
        final User user = new UserBuilder().setUsername(userName).setName(name).setEmail(email).createUser();
        setDefaults(user); //TODO
        try {
            userDAO.save(user);
            return Response.ok("Saved entry= " + user).build();
        } catch (Exception e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }

    public String getAll() {
        return userDAO.findAll().toString();
    }

    @Deprecated
    public void setDefaults(User item) {
        Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        item.setDateCreated(date);
        item.setDateEdited(date);
        item.setDateEdited(date);
        item.setDateLastused(date);
    }
}
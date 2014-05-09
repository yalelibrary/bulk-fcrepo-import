package edu.yale.library.ladybird.web.http;


import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserBuilder;
import edu.yale.library.ladybird.kernel.JobModule;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.kernel.events.UserGeneratedEvent;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.slf4j.Logger;

import javax.inject.Inject;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Http interface to manage users
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

    @DELETE
    @Produces(APPLICATION_JSON)
    public Response deleteAll() {
        try {
            logger.debug("Removing all users.");
            final List<User> userList = userDAO.findAll();
            userDAO.delete(userList);
            return Response.ok("Deleted all entries.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(@FormParam("username") String userName,
                         @FormParam("name") String name,
                         @FormParam("email") String email) {
        final User user = new UserBuilder().setUsername(userName).setName(name).setEmail(email).createUser();

        setDateDefaults(user); //TODO

        try {
            userDAO.save(user);
            //Fire event:
            KernelBootstrap kernelBootstrap = new KernelBootstrap();
            kernelBootstrap.setAbstractModule(new JobModule());
            KernelBootstrap.postEvent(new UserGeneratedEvent() {
                @Override
                public String getPrincipal() {
                    return user.getUsername();
                }

                @Override
                public String getValue() {
                    return user.getUsername();
                }

                @Override
                public String getEventName() {
                    return "user.create";
                }
            });
            return Response.ok("Saved entry= " + user).build();
        } catch (Exception e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }

    private String getAll() {
        return userDAO.findAll().toString();
    }

    private void setDateDefaults(final User item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setDate(date);
        item.setDateCreated(date);
        item.setDateEdited(date);
        item.setDateEdited(date);
        item.setDateLastused(date);
    }
}
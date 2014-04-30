package edu.yale.library.ladybird.web.http;


import edu.yale.library.ladybird.entity.ImportSource;
import edu.yale.library.ladybird.entity.ImportSourceBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDAO;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/{path: .*}/importsource")
public class ImportSourcerHttpService {
    private final Logger logger = getLogger(this.getClass());

    ImportSourceDAO dao;

    @Inject
    public ImportSourcerHttpService(ImportSourceDAO userDAO) {
        this.dao = userDAO;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAllItems() {
        return Response.ok(getAll()).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(@QueryParam("url") String url,
                         @QueryParam("prefix") String prefix) {
        final ImportSource item = new ImportSourceBuilder().setUrl(url)
                .setXmlType(0).setGetPrefix(prefix).createImportSource();

        item.setActive(true);
        setDefaults(item);

        //Mark old as inactive
        final List<ImportSource> list = getItemList();

        for (ImportSource importSource: list) {
            importSource.setActive(false);
        }

        list.add(item);

        try {
            dao.saveOrUpdateList(list);
            return Response.ok("Saved entry= " + item).build();
        } catch (Throwable e) {
            logger.error("Error saving entry={}", e);
            return Response.ok("Failed to save entry. " + e.getMessage()).build();
        }
    }

    public String getAll() {
        return dao.findAll().toString();
    }

    private List<ImportSource> getItemList() {
        List<ImportSource> list = dao.findAll();
        return list;
    }

    @Deprecated
    public void setDefaults(final ImportSource item) {
        final Date date = new Date(System.currentTimeMillis());
        item.setCreatedDate(date);
    }
}
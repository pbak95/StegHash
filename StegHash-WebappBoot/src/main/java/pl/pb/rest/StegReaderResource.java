package pl.pb.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Patryk on 11/12/2017.
 */
@Path("/")
public interface StegReaderResource {

    @GET
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    Response getUserMessages(@QueryParam("username") String username,
                             @QueryParam("pageNumber") int pageNumber);
}

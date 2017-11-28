package pl.pb.rest;


import pl.pb.jsonMappings.UserMessagesRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Patryk on 11/12/2017.
 */
@Path("/")
public interface StegReaderResource {

    @POST
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    Response getUserMessages(UserMessagesRequest messagesRequest);
}

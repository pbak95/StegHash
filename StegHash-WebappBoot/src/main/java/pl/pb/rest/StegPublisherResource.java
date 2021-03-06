package pl.pb.rest;


import pl.pb.jsonMappings.PublishMessage;
import pl.pb.jsonMappings.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Patryk on 10/18/2017.
 */
@Path("/")
public interface StegPublisherResource {

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response publishHiddenMessage(PublishMessage publishMessage);

}

package pl.pb.rest;


import pl.pb.jsonMappings.PublishMessage;

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

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    String test();
}

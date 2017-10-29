package com.mrozekma.atlassian.bitbucket.projectFields.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource of message.
 */
@Path("/fields")
public class ProjectFieldsRestResource {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMessage(@QueryParam("n") int n)
    {
       return Response.ok(new ProjectFieldsRestResourceModel("Hello World", n)).build();
    }
}
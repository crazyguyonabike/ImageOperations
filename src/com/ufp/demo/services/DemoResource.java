package com.ufp.demo.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;

import java.io.InputStream;
import java.net.URI;

import com.sun.jersey.spi.resource.Singleton;
import com.sun.jersey.spi.inject.Inject;

import com.ufp.demo.operations.OperationManager;

import org.apache.log4j.Logger;

@Singleton 
@Path("/demoservice/")
public class DemoResource {
    private static Logger logger = Logger.getLogger(DemoResource.class);

    @Context 
    private UriInfo uriInfo;

    @Inject 
    private OperationManager operationManager;

    @Path("{id}/{operation}/")
    @GET
    @Produces("image/jpeg") // any way to determine return type from internally?
    public Response getImage(@PathParam("id") String id, @PathParam("operation") String operation) {
	logger.debug("looking for id: " + id + ", operation: " + operation);
	int index = Integer.parseInt(id);
	InputStream inputStream = operationManager.process(operation, index);
	return ((inputStream !=null)?Response.ok(inputStream).build():Response.status(Response.Status.BAD_REQUEST).build());
    }
    
    @Path("add/")
    @POST
    @Consumes("text/plain")
    public Response putImage(String imageURL) {
	URI uri = uriInfo.getAbsolutePath();

	if (!operationManager.isHostSet()) {
	    operationManager.setHost(uri.getHost() + ((uri.getPort() > 0)?":" + uri.getPort():null));
	}
				     
	logger.debug("adding " + imageURL);
	operationManager.addURL(imageURL);
        return Response.status(Response.Status.ACCEPTED).build();
    }
 }

package com.joakim.azure.websvc;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * This interface defines the JAX-RS API for the '/redis' web application path.
 * The implementing ...Impl class is a non-annotated POJO.
 * 
 * @author Chris Joakim
 * @version 2016/07/25
 */

@Path("/redis")
public interface RedisResource {

	@GET
	@Path("{key}")
	@Produces("application/json")
	public StreamingOutput get(@PathParam("key") String key);

	@POST
	@Path("{key}")
	@Consumes("application/json")
	public Response set(@PathParam("key") String key, InputStream is);

}

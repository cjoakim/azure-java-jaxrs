package com.joakim.azure.websvc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.StreamingOutput;

/**
 * 
 * 
 * @author Chris Joakim
 * @version 2016/07/25
 */

@Path("/ping")
public interface PingResource {

	@GET
	@Produces("text/plain")
	public StreamingOutput ping();
}

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
 * 
 * 
 * @author  Chris Joakim
 * @version 2016/07/26
 */

@Path("/storage")
public interface StorageResource {

	@GET
	@Path("blob/{container}/{blobname}")
	@Produces("application/json")
	public StreamingOutput getBlobText(
			@PathParam("container") String container, 
			@PathParam("blobname")  String blobname);

	@POST
	@Path("blob/{container}/{blobname}")
	@Consumes("application/json")
	public Response setBlobText(
			@PathParam("container") String container, 
			@PathParam("blobname")  String blobname, 
			InputStream is);
	
}
package com.joakim.azure.websvc;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 * 
 * 
 * @author  Chris Joakim
 * @version 2016/07/25
 */

public class PingResourceImpl implements PingResource {

	public StreamingOutput ping() {
		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				String s = "ping @ " + System.currentTimeMillis();
				outputStream.write(s.getBytes());
			}
		};
	}
}

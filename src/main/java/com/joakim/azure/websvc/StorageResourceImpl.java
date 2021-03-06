package com.joakim.azure.websvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.joakim.azure.data.redis.RedisUtil;
import com.joakim.azure.data.storage.BlobUtil;

/**
 * 
 * 
 * @author  Chris Joakim
 * @version 2016/07/25
 */

public class StorageResourceImpl implements StorageResource {

	private static final Logger logger = Logger.getLogger(StorageResourceImpl.class);
	
	
	public StreamingOutput getBlobText(String container, String blobname) {
	
		try {
			BlobUtil blobUtil = new BlobUtil();			
			logger.debug("getBlobText, container: " + container + ":" + blobname);
			
			return new StreamingOutput() {
				public void write(OutputStream outputStream) throws IOException, WebApplicationException {
					// the blob is assumed to be populated with JSON content
					try {
						String json = blobUtil.getText(container, blobname);  
						if (json == null) {
							json = "{}";
						}
						outputStream.write(json.getBytes());
					}
					catch (Exception e) {
						throw new WebApplicationException();
					}
				}
			};
		}
		catch (Exception e) {
			throw new WebApplicationException();
		}
	}

	public Response setBlobText(String container, String blobname, InputStream is) {

		String json = inputStreamAsString(is);		
		Map parsed = parseJsonToMap(json);
		if (parsed != null) {
			try {
				BlobUtil blobUtil = new BlobUtil();
				String key = container + "-" + blobname;
				blobUtil.uploadFromString(container, blobname, json);
				logger.debug("set, key: " + container + "-" + blobname + " Ok, json: " + json);
				return Response.created(URI.create("/storage/" + container + "/" + blobname)).build();
			}
			catch (Exception e) {
				throw new WebApplicationException();
			}
		}
		else {
			String key = container + "-" + blobname;
			logger.debug("set, key: " + key + " Err, json: " + json);
			return Response.serverError().build();
		}
	}
	
	private String inputStreamAsString(InputStream is) {
		
		StringBuffer sb = new StringBuffer();
        try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private Map parseJsonToMap(String jsonString) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, Map.class);
		} catch (Exception e) {
			logger.debug("json parse error on: " + jsonString);
			return null;
		}
	}
}

package com.joakim.azure.websvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.joakim.azure.data.redis.RedisUtil;

/**
 * 
 * 
 * @author  Chris Joakim
 * @version 2016/07/25
 */

public class RedisResourceImpl implements RedisResource {

	private static final Logger logger = Logger.getLogger(RedisResourceImpl.class);
	
	public StreamingOutput get(String key) {
		
		RedisUtil redisUtil = new RedisUtil();
		logger.debug("get, key: " + key);
		
		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				String json = redisUtil.get(key);  // the cache is populated with JSON content
				if (json == null) {
					json = "{}";
				}
				outputStream.write(json.getBytes());
			}
		};
	}
	
	public Response set(String key, InputStream is) {
		
		String json = inputStreamAsString(is);
		
		Map parsed = parseJsonToMap(json);
		if (parsed != null) {
			RedisUtil redisUtil = new RedisUtil();
			redisUtil.set(key, json);
			logger.debug("set, key: " + key + " Ok, json: " + json);
			return Response.created(URI.create("/redis/" + key)).build();
		}
		else {
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

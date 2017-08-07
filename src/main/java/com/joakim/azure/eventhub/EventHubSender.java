package com.joakim.azure.eventhub;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import com.joakim.azure.Config;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;

/**
 * This class implements Azure EventHub operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/08/02
 */

public class EventHubSender {

	// Constants:
	private final static Logger logger = Logger.getLogger(EventHubSender.class);
	
	// Instance variables:
	private ConnectionStringBuilder connStr = null;
	private EventHubClient eventHubClient   = null;
	
	public EventHubSender() {
		
		super();
		boolean debugLogging = true;
		
		try {					
			String namespaceName = Config.envVar("AZURE_EVENTHUB_NAMESPACE");
			String eventHubName  = Config.envVar("AZURE_EVENTHUB_NAME");
			String sasKeyName    = Config.envVar("AZURE_EVENTHUB_SEND_KEYNAME");
			String sasKey        = Config.envVar("AZURE_EVENTHUB_SEND_KEYVALUE");
			if (debugLogging) {
				logger.debug("CONFIG NS: " + namespaceName);
				logger.debug("CONFIG N:  " + eventHubName);
				logger.debug("CONFIG KN: " + sasKeyName);
				logger.debug("CONFIG KV: " + sasKey);
			}
			connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);
			eventHubClient = EventHubClient.createFromConnectionString(connStr.toString()).get();
			
			if (debugLogging) {
				logger.debug("CONFIG CS: " + connStr.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean send(String json) {
		
		if (eventHubClient != null) {
			try {
				byte[] payloadBytes = json.getBytes(Charset.defaultCharset());
				EventData eventData = new EventData(payloadBytes);
				eventHubClient.send(eventData).get();
				logger.debug("sent: " + json);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public String connectionString() {
		
		return connStr.toString();
	}
	
	public boolean close() {

		if (eventHubClient != null) {
			eventHubClient.close();
		}
		return false;
	}
}

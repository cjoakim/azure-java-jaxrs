package com.joakim.azure.eventhub;

import java.util.concurrent.ExecutionException;

/**
*
* com.joakim.azure.eventhub.EventHubProcessorMain
* 
* @author Chris Joakim, Microsoft
* @date   2016/08/03
*/

import org.apache.log4j.Logger;

import com.joakim.azure.Config;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;

public class EventHubProcessorMain {
	
	private static final Logger logger = Logger.getLogger(EventHubProcessorMain.class);
	
	private static String[] clArgs = null;
	private static boolean debugLogging = true;
	private static ConnectionStringBuilder connStr = null;
	
	public static void main(String[] args) {

		try {					
			String namespaceName = Config.envVar("AZURE_EVENTHUB_NAMESPACE");
			String eventHubName  = Config.envVar("AZURE_EVENTHUB_NAME");
			String sasKeyName    = Config.envVar("AZURE_EVENTHUB_MANAGE_KEYNAME");
			String sasKey        = Config.envVar("AZURE_EVENTHUB_MANAGE_KEYVALUE");
			
	    	String consumerGroupName = "$Default"; // "consumergroup1"; //"$Default";
	    	String storageAcct = Config.envVar("AZURE_STORAGE_ACCOUNT").trim();
	    	String storageKey  = Config.envVar("AZURE_STORAGE_ACCESS_KEY").trim();
	    	String storageConnString = "DefaultEndpointsProtocol=https;AccountName=" + storageAcct + ";AccountKey=" + storageKey;
	    	
			if (debugLogging) {
				logger.debug("CONFIG NS:  " + namespaceName);
				logger.debug("CONFIG N:   " + eventHubName);
				logger.debug("CONFIG KN:  " + sasKeyName);
				logger.debug("CONFIG KV:  " + sasKey);
				logger.debug("CONFIG SA:  " + storageAcct);
				logger.debug("CONFIG SK:  " + storageKey);
				logger.debug("CONFIG SCS: " + storageConnString);
			}
			connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);
			logger.debug("connStr: " + connStr.toString());
			
			EventProcessorHost host = new EventProcessorHost(eventHubName, consumerGroupName, connStr.toString(), storageConnString);
			
			logger.debug("Registering host named: " + host.getHostName());
			logger.debug("ehcs: " + host.getEventHubConnectionString());

			EventProcessorOptions options = new EventProcessorOptions();
			options.setExceptionNotification(new ErrorNotificationHandler());
			
			try {
				// The Future returned by the register* APIs completes when initialization is done and
				// message pumping is about to start. It is important to call get() on the Future because
				// initialization failures will result in an ExecutionException, with the failure as the
				// inner exception, and are not otherwise reported.
				host.registerEventProcessor(EventProcessor.class, options).get();
			}
			catch (Exception e) {
				logger.debug("Failure while registering: ");
				if (e instanceof ExecutionException) {
					Throwable inner = e.getCause();
					logger.debug(inner.toString());
				}
				else {
					logger.debug(e.toString());
				}
			}
			
	        System.err.println("Press enter to stop");
	        try {
	            System.in.read();
		        System.err.println("stopping...");
		        
	            // Processing of events continues until unregisterEventProcessor is called. Unregistering shuts down the
	            // receivers on all currently owned leases, shuts down the instances of the event processor class, and
	            // releases the leases for other instances of EventProcessorHost to claim.
	            logger.debug("Calling unregister");
	            host.unregisterEventProcessor();
	            
	            // There are two options for shutting down EventProcessorHost's internal thread pool: automatic and manual.
	            // Both have their advantages and drawbacks. See the JavaDocs for setAutoExecutorShutdown and forceExecutorShutdown
	            // for more details. This example uses forceExecutorShutdown because it is the safe option, at the expense of
	            // another line of code.
	            logger.debug("Calling forceExecutorShutdown");
	            EventProcessorHost.forceExecutorShutdown(120);
	        }
	        catch(Exception e) {
	        	logger.debug(e.toString());
	            e.printStackTrace();
	        }
	        logger.debug("End of sample");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String connectionString() {
		
		return connStr.toString();
	}
	
}

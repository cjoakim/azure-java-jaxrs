package com.joakim.azure.data.storage;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

/**
 * This class implements Azure Queue Storage operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public class QueueUtil extends StorageUtil {

	// Constants:
	private final static Logger logger = Logger.getLogger(QueueUtil.class);
	
	// Instance variables:
	CloudQueueClient queueClient = null;
	
	
	/**
	 * Default constructor; config values come from environment variables.
	 */
	public QueueUtil() throws Exception {
		
		super();
		queueClient = storageAccount.createCloudQueueClient();
	}
	
    public CloudQueue getQueue(String name, boolean createIfAbsent) throws URISyntaxException, StorageException {
    	
    	CloudQueue queue = queueClient.getQueueReference(name);
        if (queue.exists()) {
        	return queue;
        }
        else {
        	if (createIfAbsent) {
        		queue.createIfNotExists();
        	}
        }
        return queue;
    }
    
    public void putMessage(String qname, String messageText) throws URISyntaxException, StorageException {
    	
    	CloudQueue queue = getQueue(qname, true);
    	
    	CloudQueueMessage message = new CloudQueueMessage(messageText);
        queue.addMessage(message);
        logger.debug("putMessage: " + message.getMessageId() + " -> " + messageText);
    }
    
    public CloudQueueMessage getMessage(String qname) throws URISyntaxException, StorageException {
    	
    	CloudQueue queue = getQueue(qname, true);
    	return queue.retrieveMessage();
    }
    
    public long getQueueLength(String qname) throws URISyntaxException, StorageException {
    	
    	CloudQueue queue = getQueue(qname, true);
    	queue.downloadAttributes();
        return queue.getApproximateMessageCount();
    }
}

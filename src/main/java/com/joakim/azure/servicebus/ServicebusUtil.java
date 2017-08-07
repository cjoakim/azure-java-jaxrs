package com.joakim.azure.servicebus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.joakim.azure.Config;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.CreateQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.GetQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.ListQueuesResult;
import com.microsoft.windowsazure.services.servicebus.models.QueueInfo;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;

/**
 * This class implements Azure ServiceBus operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public class ServicebusUtil {

	// Constants:
	private final static Logger logger = Logger.getLogger(ServicebusUtil.class);
	
	// Instance variables:
	private Configuration      sbConfig = null;
	private ServiceBusContract service  = null;

	
	public ServicebusUtil() {
		
		super();
		
		try {
			String namespace  = Config.envVar("AZURE_SERVICEBUS_NAMESPACE");
			String keyName    = Config.envVar("AZURE_SERVICEBUS_KEY_NAME");
			String accessKey  = Config.envVar("AZURE_SERVICEBUS_ACCESS_KEY");
			String urlSuffix  = ".servicebus.windows.net";

			sbConfig =
				ServiceBusConfiguration.configureWithSASAuthentication(
				    namespace, keyName, accessKey, urlSuffix);
			service = ServiceBusService.create(sbConfig);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public boolean createQueue(String qname) {
     
    	// TODO - revisit this method.  detect that the queue exists first?
    	
    	try {
			logger.debug("createQueue - name: " + qname);
			QueueInfo queueInfo = new QueueInfo(qname);
			service.createQueue(queueInfo);
			return true;
		} 
    	catch (Throwable e) {
			// com.sun.jersey.api.client.UniformInterfaceException: 
			// PUT https://joakimbus1.servicebus.windows.net/joakimqueue4?api-version=2013-07 
			// returned a response status of 409 Conflict
			// e.printStackTrace();
    		return false;
		}
    }
    
    public void deleteQueue(String qname) {
     	
    	try {
			logger.debug("deleteQueue - name: " + qname);
			service.deleteQueue(qname);
		} 
    	catch (ServiceException e) {
    		e.printStackTrace();
		}
    }
    
    public List<QueueInfo> listQueues() throws ServiceException {
    	
    	ListQueuesResult result = service.listQueues();
    	if (result != null) {
    		return result.getItems();
    	}
    	else {
    		return new ArrayList<QueueInfo>();
    	}
    }
    
    public long queueDepth(String qname) {
    	
    	long depth = -1;
    	
    	try {
			GetQueueResult result = service.getQueue(qname);
			if (result != null) {
				QueueInfo qinfo = result.getValue();
				if (qinfo != null) {
					depth = qinfo.getMessageCount();
				}
			}
		} 
    	catch (ServiceException e) {
			e.printStackTrace();
		}
    	return depth;
    }
    
    public boolean putMessageOnQueue(String qname, String messageText) {

    	try {
    	    BrokeredMessage message = new BrokeredMessage(messageText);
    	    service.sendQueueMessage(qname, message);
    	    return true;
    	}
    	catch (ServiceException e) {
			// e.printStackTrace();
    		return false;
    	}
    }
    
    public BrokeredMessage getMessageFromQueue(String qname) {

    	try {
    		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
    	    opts.setReceiveMode(ReceiveMode.RECEIVE_AND_DELETE);
    	    ReceiveQueueMessageResult result = service.receiveQueueMessage(qname, opts);
    	    if (result != null) {
    	    	return result.getValue();
    	    }
    	}
    	catch (ServiceException e) {
			// e.printStackTrace();
    	}
    	return null;
    }
    
    public String readMessageBody(BrokeredMessage bm) throws IOException {

		if (bm != null) {
			StringBuffer sb = new StringBuffer();
			boolean continueToRead = true;
			InputStream bodyStream = bm.getBody();
            while (continueToRead) {
            	byte[] ba = new byte[200];
            	int bytesRead = bodyStream.read(ba);
            	if ((bytesRead != -1)) {
            		sb.append(new String(ba).trim());
            	}
            	else {
            		continueToRead = false;
            	}
            }
            return sb.toString();
		}
		else {
			return null;
		}
    }
    
}

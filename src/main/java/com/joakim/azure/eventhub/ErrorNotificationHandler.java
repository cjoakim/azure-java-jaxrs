package com.joakim.azure.eventhub;

import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;

/**
 *
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/08/03
 */

public class ErrorNotificationHandler implements Consumer<ExceptionReceivedEventArgs> {

	// Constants:
	private final static Logger logger = Logger.getLogger(ErrorNotificationHandler.class);
	
	@Override
	public void accept(ExceptionReceivedEventArgs t) {
		
		logger.debug("Host " + t.getHostname() + " received general error notification during " + t.getAction() + ": " + t.getException().toString());
	}
}

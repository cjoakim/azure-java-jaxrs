package com.joakim.azure.eventhub;

import org.apache.log4j.Logger;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

/**
*
* 
* @author Chris Joakim, Microsoft
* @date   2016/08/03
*/

public class EventProcessor implements IEventProcessor {

	// Constants:
	private final static Logger logger = Logger.getLogger(EventProcessor.class);
	
	private int checkpointBatchingCount = 0;

	// OnOpen is called when a new event processor instance is created by the host. In a real implementation, this
	// is the place to do initialization so that events can be processed when they arrive, such as opening a database
	// connection.
	@Override
    public void onOpen(PartitionContext context) throws Exception {
		
    	logger.debug("onOpen partition " + context.getPartitionId() + " is opening");
    }

    // OnClose is called when an event processor instance is being shut down. The reason argument indicates whether the shut down
    // is because another host has stolen the lease for this partition or due to error or host shutdown. In a real implementation,
    // this is the place to do cleanup for resources that were opened in onOpen.
	@Override
    public void onClose(PartitionContext context, CloseReason reason) throws Exception {
		
		logger.debug("onClose partition " + context.getPartitionId() + " is closing for reason " + reason.toString());
    }
	
	// onError is called when an error occurs in EventProcessorHost code that is tied to this partition, such as a receiver failure.
	// It is NOT called for exceptions thrown out of onOpen/onClose/onEvents. EventProcessorHost is responsible for recovering from
	// the error, if possible, or shutting the event processor down if not, in which case there will be a call to onClose. The
	// notification provided to onError is primarily informational.
	@Override
	public void onError(PartitionContext context, Throwable error) {
		
		logger.debug("onError partition " + context.getPartitionId() + " onError: " + error.toString());
	}

	// onEvents is called when events are received on this partition of the Event Hub. The maximum number of events in a batch
	// can be controlled via EventProcessorOptions. Also, if the "invoke processor after receive timeout" option is set to true,
	// this method will be called with null when a receive timeout occurs.
	@Override
    public void onEvents(PartitionContext context, Iterable<EventData> messages) throws Exception {
		
		logger.debug("onEvents partition " + context.getPartitionId() + " got message batch");
        int messageCount = 0;
        
        for (EventData data : messages) {
        	logger.debug("onEvents (" + context.getPartitionId() + "," + data.getSystemProperties().getOffset() + "," +
            		data.getSystemProperties().getSequenceNumber() + "): " + new String(data.getBody(), "UTF8"));
            messageCount++;
            
            // Checkpointing persists the current position in the event stream for this partition and means that the next
            // time any host opens an event processor on this event hub+consumer group+partition combination, it will start
            // receiving at the event after this one. Checkpointing is usually not a fast operation, so there is a tradeoff
            // between checkpointing frequently (to minimize the number of events that will be reprocessed after a crash, or
            // if the partition lease is stolen) and checkpointing infrequently (to reduce the impact on event processing
            // performance). Checkpointing every five events is an arbitrary choice for this sample.
            this.checkpointBatchingCount++;
            if ((checkpointBatchingCount % 5) == 0) {
            	logger.debug("onEvents partition " + context.getPartitionId() + " checkpointing at " +
           			data.getSystemProperties().getOffset() + "," + data.getSystemProperties().getSequenceNumber());
            	context.checkpoint(data);
            }
        }
        logger.debug("onEvents partition " + context.getPartitionId() + " batch size was " + messageCount + " for host " + context.getOwner());
    }
}

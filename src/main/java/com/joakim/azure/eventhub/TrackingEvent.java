package com.joakim.azure.eventhub;

/**
 * Instances of this class represent one Tracking Event, or telemetry event, that is put on the EventHub.
 * It is a simple POJO class.  Class TrackingEventGenerator creates instances for testing.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/08/03
 */

public class TrackingEvent {

	// Instance variables:
	public String id    = null;
	public long   epoch = 0;
	public long   seq   = 0;
	public double lat   = 0.0;
	public double lng   = 0.0;
	public int    alt   = 0;
	public String host  = null;
	public String uuid  = null;
	
	public TrackingEvent() {
		
		super();
	}
}

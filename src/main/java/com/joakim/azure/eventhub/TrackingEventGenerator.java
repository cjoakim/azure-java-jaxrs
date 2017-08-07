package com.joakim.azure.eventhub;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/08/02
 */

public class TrackingEventGenerator {

	// Constants:
	private final static Logger logger = Logger.getLogger(TrackingEventGenerator.class);

	// Class variables:
	private static Map<String, TrackingEvent> eventMap = new HashMap<String, TrackingEvent>();
	private static ArrayList<String> trackedIds = new ArrayList<String>();
	private static Random random   = new Random();
	private static String hostname = null;

	/**
	 * This method is intentionally private; use the static class methods
	 * instead.
	 */
	private TrackingEventGenerator() {

		super();

	}

	public static void seed(int count) {

		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} 
		catch (UnknownHostException e) {
			hostname = "unknown";
		}
		
		for (int i = 0; i < count; i++) {
			TrackingEvent e = new TrackingEvent();
			String id = "" + i;
			e.id  = id;
			e.lat = 35.49794752644903;
			e.lng = -80.84903508424759;
			e.host = hostname;
			eventMap.put(id, e);
			trackedIds.add(id);
			logger.debug("seed id: " + id);
		}
	}

	public static TrackingEvent nextEvent() {

		String id = selectRandomId();
		TrackingEvent e = eventMap.get(id);
		e.seq++;
		e.epoch = Instant.now().toEpochMilli();
		e.uuid  = UUID.randomUUID().toString();
		updateAltitude(e);
		updateGpsCoordinates(e);
		return e;
	}

	private static String selectRandomId() {

		int idx = random.nextInt(trackedIds.size());
		return trackedIds.get(idx);
	}

	private static void updateAltitude(TrackingEvent e) {

		if (random.nextBoolean()) {
			e.alt = e.alt + 1;
		}
		else {
			e.alt = e.alt - 1;
		}
		
		if (e.alt < 0) {
			e.alt = 0;
		}
		if (e.alt > 10) {
			e.alt = 10;
		}
	}
	
	private static void updateGpsCoordinates(TrackingEvent e) {
	
		double latDiff = random.nextDouble();
		double lngDiff = random.nextDouble();
		
		if (random.nextBoolean()) {
			e.lat = e.lat + latDiff;
		}
		else {
			e.lat = e.lat - latDiff;
		}
		
		if (random.nextBoolean()) {
			e.lng = e.lng + lngDiff;
		}
		else {
			e.lng = e.lng - lngDiff;
		}
	}

}

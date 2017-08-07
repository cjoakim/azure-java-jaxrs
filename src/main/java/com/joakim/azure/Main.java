package com.joakim.azure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joakim.azure.data.redis.RedisUtil;
import com.joakim.azure.data.storage.BlobUtil;
import com.joakim.azure.data.storage.QueueUtil;
import com.joakim.azure.data.storage.TableUtil;
import com.joakim.azure.data.storage.ZipCodeEntity;
import com.joakim.azure.eventhub.EventHubSender;
import com.joakim.azure.eventhub.TrackingEvent;
import com.joakim.azure.eventhub.TrackingEventGenerator;
import com.joakim.azure.servicebus.ServicebusUtil;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableResult;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.QueueInfo;

/**
 * This proof-of-concept class is simply used to execute, from the command-line, 
 * the other classes in this project.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/08/03
 */

public class Main implements EnvVarNames {

	private static final String POSTAL_CODES_CSV_PATH = "data/postal_codes_nc.csv";
	private static final Logger logger = Logger.getLogger(Main.class);
	
	private static String[] clArgs = null;
	
	public static void main(String[] args) {
		
		clArgs = args;
		long startEpoch = System.currentTimeMillis();

		try {
			Config.getInstance();
			if (booleanFlag("--blob")) {
				doBlobStorage();
			}
			if (booleanFlag("--hdinsight-blob")) {
				doBlobStorageForHdinsight();
			}
			if (booleanFlag("--table")) {
				doTableStorage();
			}
			if (booleanFlag("--queue")) {
				doQueueStorage();
			}
			if (booleanFlag("--redis")) {
				doRedisCache();
			}
			if (booleanFlag("--redis-get")) {
				doRedisCacheGet();
			}
			if (booleanFlag("--servicebus")) {
				doServiceBus();
			}
			if (booleanFlag("--event-hub-gen-events")) {
				doEventHubGenerateEvents();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			long elapsed = System.currentTimeMillis() - startEpoch;
			logger.debug("main() completed in " + elapsed + " milliseconds.");
		}
	}
	
	private static void doBlobStorage() {
		
		try {
			BlobUtil util = new BlobUtil();
			String cname = "test1";
			util.getContainer(cname, true);
			util.uploadFromFile(cname, "date1", "data/date1.txt");
			util.uploadFromFile(cname, "date2", "data/date2.txt");
			util.uploadFromString(cname, "epoch", "" + System.currentTimeMillis());
			
			util.listAllBlobs("test");
			util.deleteBlob(cname, "date0.txt");
			util.listBlobsInContainer(cname);
			util.listBlobsInContainer("not-there");
			
			util.downloadToFile(cname, "date2", "tmp/date2.txt");
			util.downloadToFile(cname, "epoch", "tmp/epoch.txt"); 
			
			if (booleanFlag("--zipcode-files")) {
				cname = "zipcode-files";
				util.getContainer(cname, true);
				util.uploadFromFile(cname, "postal_codes_nc.csv",  "data/postal_codes_nc.csv");
				util.uploadFromFile(cname, "postal_codes_nc.json", "data/postal_codes_nc.json");
				util.downloadToFile(cname, "postal_codes_nc.csv",  "tmp/postal_codes_nc.csv");
				util.downloadToFile(cname, "postal_codes_nc.json", "tmp/postal_codes_nc.json"); 
				util.listAllBlobs("test");
				util.listBlobsInContainer(cname);
			}
			
			if (booleanFlag("--load-individual-zipcode-json-blobs")) {
				String rawLine = null;
				String csvLine = null;
				cname = "zipcodes";
				util.getContainer(cname, true);
				
				try (BufferedReader br = new BufferedReader(new FileReader(POSTAL_CODES_CSV_PATH))) {
				    while ((rawLine = br.readLine()) != null) {
						try {
							String jsonString = zipCodeCsvToJson(rawLine);
							ObjectMapper mapper = new ObjectMapper();
							Map<String, String> zipObj = mapper.readValue(jsonString, Map.class);
							String bname = zipObj.get("zip_code");
							if (bname != null) {
								if (bname.length() > 0) {
									util.uploadFromString(cname, bname, jsonString);
									logger.debug("uploaded: " + cname + ":" + bname + " -> " + jsonString);
								}
							}
						}
						catch (Exception e) {
							logger.debug("bad csv line: " + rawLine);
							e.printStackTrace();
						}
				    }
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doBlobStorageForHdinsight() {
	
		logger.debug("doBlobStorageForHdinsight start...");
		try {
			BlobUtil util = new BlobUtil();
			String cname = "cjoakimhd";
			util.getContainer(cname, true);
			
			util.uploadFromFile(cname, "postal_codes_nc.csv",  "data/postal_codes_nc.csv");
			util.uploadFromFile(cname, "postal_codes_nc.json", "data/postal_codes_nc.json");
			
			util.listAllBlobs("cjoakimhd");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doTableStorage() {
		
		try {
			TableUtil util = new TableUtil();
			String rawLine = null;
			String csvLine = null;
			String pkey    = null;
			ZipCodeEntity entity = null;
			
			util.deleteTable("test1");
			
			CloudTable table = util.getTable(TableUtil.ZIPCODES_TABLE, true);
			logger.debug("table uri: " + table.getUri());
			
			util.listAllTables();
			
			if (booleanFlag("--load-zipcode-table")) {
				try (BufferedReader br = new BufferedReader(new FileReader(POSTAL_CODES_CSV_PATH))) {
				    while ((rawLine = br.readLine()) != null) {
				    	csvLine = rawLine.replace('|', ',');
				    	logger.debug("csvLine: " + csvLine);
						try {
							TableResult result = util.insertZipCode(csvLine);
							logger.debug("result: " + result.getHttpStatusCode() + " " + result.getEtag());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
				    }
				}
			}
			
			pkey = "06";
			logger.debug("queryZipcodesPartition: " + pkey);
			util.queryZipcodesPartition(pkey);

			pkey = "28";
			logger.debug("queryZipcodesPartition: " + pkey);
			util.queryZipcodesPartition(pkey);
			
			entity = util.lookupZipcode("28036");
			logger.debug("entity: " + entity);
			
			entity = util.lookupZipcode("27612");
			logger.debug("entity: " + entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doQueueStorage() {
		
		try {
			QueueUtil util = new QueueUtil();
			String qname   = "queue1";
			String csvLine = null;
			
			CloudQueue queue = util.getQueue(qname, true);
			logger.debug("queue uri: " + queue.getUri());
			
			util.putMessage(qname, "test message at " + System.currentTimeMillis());
			long length = util.getQueueLength(qname);
	        logger.debug("length: " + qname + " -> " + length);
	        
	        CloudQueueMessage message = util.getMessage(qname);
	        if (message != null) {
	        	String msgId   = message.getMessageId();
	        	String msgText = message.getMessageContentAsString();
	            logger.debug(String.format("queue: %s msgId: %s -> %s", qname, msgId, msgText));
	            queue.deleteMessage(message);
	        }
	        
			if (booleanFlag("--enqueue-zipcode-messages")) {
				try (BufferedReader br = new BufferedReader(new FileReader(POSTAL_CODES_CSV_PATH))) {
				    while ((csvLine = br.readLine()) != null) {
						try {
							util.putMessage(qname, csvLine);
							logger.debug("putMessage: " + csvLine);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
				    }
				}
			}
			
			length = util.getQueueLength(qname);
	        logger.debug("length: " + qname + " -> " + length);
	        
			if (booleanFlag("--dequeue-zipcode-messages")) {
				message = null;
				do {
					message = util.getMessage(qname);
			        if (message != null) {
			        	String msgId   = message.getMessageId();
			        	String msgText = message.getMessageContentAsString();
			            logger.debug(String.format("queue: %s msgId: %s -> %s", qname, msgId, msgText));
			            queue.deleteMessage(message);
			        }
				}
				while (message != null);
			}
			
			length = util.getQueueLength(qname);
	        logger.debug("length: " + qname + " -> " + length);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doRedisCache() {
		
		try {
			RedisUtil util = new RedisUtil();
			
			if (booleanFlag("--load-cache-zipcodedata")) {
				redisPostalCodes(util, 1);
			}
		
			if (booleanFlag("--read-cache-zipcodedata")) {
				redisPostalCodes(util, 2);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void redisPostalCodes(RedisUtil util, int operationCode) throws Exception {

		long start = System.currentTimeMillis();
		
		Stream<String> stream = getTextFileLinesStream(POSTAL_CODES_CSV_PATH);
		stream.forEach(line -> {
			logger.debug("stream: " + line);
			if (line.startsWith("seq")) {
				// skip the csv header record -> "seq|zip_code|country|city|state|latitude|longitude"
			}
			else {
		    	String csvLine = line.replace('|', ',');
		    	String[] tokens = csvLine.split(",");
		    	if (tokens.length > 6) {
		    		String zipCode = tokens[1];
		    		switch (operationCode) {
		    			case 1:
		    				String json = zipCodeCsvToJson(csvLine);
		    	    		util.set(zipCode, json);
		    	            logger.debug(String.format("cached: %s -> %s", zipCode, json));
		    	            break;
		    			case 2:
		    	    		String data = util.get(zipCode);
		    	    		if (data == null) {
		    	    			logger.debug(String.format("read: %s -> %s", zipCode, "null"));
		    	    		}
		    	    		else {
		    	    			logger.debug(String.format("read: %s -> %s", zipCode, data));
		    	    		}
		    	            break; 
		    	         default:
		    	        	 logger.error("unknown operationCode: " + operationCode);
		    		}

		    	}
			}

		});
		
		long elapsed = System.currentTimeMillis() - start;
		logger.debug("elapsed ms: " + elapsed);
		
		stream.close();
		logger.debug("stream closed");
	}
	
	private static String zipCodeCsvToJson(String csvLine) {
		
		try {
			Map<String,String> jsonObj = new HashMap<>();
	    	String line = csvLine.replace('|', ',');
	    	String[] tokens = line.split(",");
	    	if (tokens.length > 6) {
	    		// "seq|zip_code|country|city|state|latitude|longitude"
	    		jsonObj.put("seq",       trimmed(tokens[0]));
	    		jsonObj.put("zip_code",  trimmed(tokens[1]));
	    		jsonObj.put("country",   trimmed(tokens[2]));
	    		jsonObj.put("city",      trimmed(tokens[3]));
	    		jsonObj.put("state",     trimmed(tokens[4]));
	    		jsonObj.put("latitude",  trimmed(tokens[5]));
	    		jsonObj.put("longitude", trimmed(tokens[6]));
	    	}
			String json = new ObjectMapper().writeValueAsString(jsonObj);
			return json;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	private static String trimmed(String s) {
		
		if (s == null) {
			return "";
		}
		else {
			return s.trim();
		}
	}
	
	private static void doRedisCacheGet() {
		
		try {
			RedisUtil util = new RedisUtil();
			String key = keywordArg("--key");
			logger.debug("doRedisCacheGet - key: " + key);
			RedisUtil redisUtil = new RedisUtil();
			String data = redisUtil.get(key);
			logger.debug("doRedisCacheGet - key: " + key + " -> " + data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doServiceBus() {
		
		try {
			String qname = "testqueue1";
			
			logger.debug("doServiceBus start");
			ServicebusUtil util = new ServicebusUtil();
			util.createQueue(qname);
			util.createQueue("testqueue2");
			util.createQueue("testqueue3");
			util.createQueue("temp");
			
			int readCount = 24;
			int putCount  = 20;
			
			for (int i = 0; i < readCount; i++) {
				BrokeredMessage bm = util.getMessageFromQueue(qname);
				if (bm != null) {
					String id = bm.getMessageId();
					String body = util.readMessageBody(bm);
					logger.debug(String.format("getMessageFromQueue; id: %s body: %s", id, body));
				}
			}
			
			for (int i = 0; i < putCount; i++) {
				String msgTxt = "test message sent at " + System.currentTimeMillis();
				logger.debug(String.format("putMessageOnQueue %s -> %s", qname, msgTxt));
				util.putMessageOnQueue(qname, msgTxt);
			}
			
			// list the queues before deleting "temp".
			List<QueueInfo> queueList = util.listQueues();
			for (QueueInfo item : queueList) {
				String name = item.getEntry().getTitle();
				logger.debug("queue list; before: " + name + " uri: " + item.getUri());
			}
			
			util.deleteQueue("temp");
			
			// list the queues, with their depth, after deleting "temp".
			queueList = util.listQueues();
			for (QueueInfo item : queueList){
				String name = item.getEntry().getTitle();
				long depth = util.queueDepth(name);
				logger.debug("queue list; after: " + name + " depth: " + depth);
			}
			logger.debug("doServiceBus finish!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void doEventHubGenerateEvents() {

		int  instanceCount = Integer.parseInt(keywordArg("--instances"));
		int  maxEvents  = Integer.parseInt(keywordArg("--max-events"));
		long eventCount = 0;
		
		TrackingEventGenerator.seed(instanceCount);
		Gson gson = new GsonBuilder().create();
		EventHubSender sender = new EventHubSender();
		
		while (eventCount < maxEvents) {
			eventCount++;
			TrackingEvent e = TrackingEventGenerator.nextEvent();
			sender.send(gson.toJson(e));
		}
		sender.close();
	}
	
	private static boolean booleanFlag(String flag) {
		
		for (int i = 0; i < clArgs.length; i++) {
			if (clArgs[i].equals(flag)) {
				return true;
			}
		}
		return false;
	}
	
	private static String keywordArg(String flag) {
		
		for (int i = 0; i < clArgs.length; i++) {
			if (clArgs[i].equals(flag)) {
				return clArgs[i+1];
			}
		}
		return "";
	}
	
	private static Stream<String> getTextFileLinesStream(String path) throws IOException {
		
		return Files.lines(Paths.get(path));
	}
	
	private static void pause(long milliseconds) {
		
		try {
			logger.debug("pause: " + milliseconds);
			Thread.currentThread().sleep(milliseconds);
		}
		catch (InterruptedException e) {
			// ignore
		}
	}
	
}


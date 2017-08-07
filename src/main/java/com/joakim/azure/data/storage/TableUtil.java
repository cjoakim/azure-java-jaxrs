package com.joakim.azure.data.storage;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.microsoft.azure.storage.table.TableResult;

/**
 * This class implements Azure Table Storage operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public class TableUtil extends StorageUtil {

	// Constants:
	private final static Logger logger = Logger.getLogger(TableUtil.class);
	
	public static final String ZIPCODES_TABLE = "zipcodes";
	
	public static final String PARTITION_KEY  = "PartitionKey";
	public static final String ROW_KEY        = "RowKey";
	public static final String TIMESTAMP      = "Timestamp";

	// Instance variables:
	CloudTableClient tableClient = null;
	
	/**
	 * Default constructor; config values come from environment variables.
	 */
	public TableUtil() throws Exception {
		
		super();
        tableClient = storageAccount.createCloudTableClient();
	}
	
    public CloudTable getTable(String name, boolean createIfAbsent) throws URISyntaxException, StorageException {

    	CloudTable table = tableClient.getTableReference(name);
    	
        if (table.exists()) {
        	return table;
        }
        else {
        	if (createIfAbsent) {
        		table.createIfNotExists();
        	}
        }
        return table;
    }
    
    public boolean deleteTable(String name) throws Exception {

    	CloudTable table = tableClient.getTableReference(name);
    	return table.deleteIfExists();
    }
    
    public void listAllTables() {

    	for (String tname : tableClient.listTables()) {
        	logger.debug(String.format("table: %s", tname));

    	}
    }
    
    public TableResult insertZipCode(String csvLine) throws URISyntaxException, StorageException {
    	
    	CloudTable table = getTable(ZIPCODES_TABLE, true);
		ZipCodeEntity entity = new ZipCodeEntity(csvLine, true);
		TableOperation operation = TableOperation.insertOrReplace(entity);
	    return table.execute(operation);
    }
    
    public void queryZipcodesPartition(String pkey) throws URISyntaxException, StorageException {
    	
    	if (pkey != null) {
        	CloudTable table = getTable(ZIPCODES_TABLE, true);
        	String filter = TableQuery.generateFilterCondition(PARTITION_KEY, QueryComparisons.EQUAL, pkey);
        	
        	TableQuery<ZipCodeEntity> query = TableQuery.from(ZipCodeEntity.class).where(filter);

        	for (ZipCodeEntity e : table.execute(query)) {
        		logger.debug(String.format("%s %s, %s %s", e.getPartitionKey(), e.getRowKey(), e.getCity(), e.getLatitude()));
        	}
    	}
    }
    
    public ZipCodeEntity lookupZipcode(String zipCode) throws URISyntaxException, StorageException {
    	
    	if (zipCode != null) {
    		String trimmed = zipCode.trim();
    		if (trimmed.length() > 2) {
    			String pkey = trimmed.substring(0, 2);
    			String rkey = trimmed;
    	    	CloudTable table = getTable(ZIPCODES_TABLE, true);
    	    	TableOperation operation = TableOperation.retrieve(pkey, rkey, ZipCodeEntity.class);
    	    	return table.execute(operation).getResultAsType();
    		}
    	}
    	return null;
    }
}

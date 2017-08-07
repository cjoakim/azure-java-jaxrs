package com.joakim.azure.data.storage;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.log4j.Logger;

import com.joakim.azure.Config;
import com.microsoft.azure.storage.CloudStorageAccount;

/**
 * This is the abstract superclass of the several Util classes in this package.
 * It contains standard/reusable logic for establishing a CloudStorageAccount object.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public abstract class StorageUtil {

	// Constants:
	private final static Logger logger = Logger.getLogger(StorageUtil.class);
	
	// Instance variables:
	protected CloudStorageAccount storageAccount = null;
	
	/**
	 * Default constructor; config values come from environment variables.
	 */
	public StorageUtil() throws Exception {
		
		super();
		
        try {
        	String connString = Config.storageConnectionString();
            storageAccount = CloudStorageAccount.parse(connString);
        }
        catch (IllegalArgumentException|URISyntaxException e) {
            logger.debug("Invalid URI in Connection string");
            throw e;
        }
        catch (InvalidKeyException e) {
            logger.debug("Invalid key in Connection string");
            throw e;
        }
	}
	
}

package com.joakim.azure.data.storage;

import org.apache.log4j.Logger;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

/**
 * This class implements Azure Blob Storage operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/17
 */

public class BlobUtil extends StorageUtil {

	// Constants:
	private final static Logger logger = Logger.getLogger(BlobUtil.class);
	
	// Instance variables:
	private CloudBlobClient blobClient = null;
	
	/**
	 * Default constructor; config values come from environment variables.
	 */
	public BlobUtil() throws Exception {
		
		super();
		blobClient = storageAccount.createCloudBlobClient();
	}
	
    public CloudBlobContainer getContainer(String name, boolean createIfAbsent) throws Exception {

        CloudBlobContainer container = blobClient.getContainerReference(name);
        if (container.exists()) {
        	return container;
        }
        else {
        	if (createIfAbsent) {
        		container.createIfNotExists();
        	}
        }
        return container;
    }
    
    public void uploadFromFile(String cname, String bname, String fname) throws Exception {

        CloudBlockBlob blob = getContainer(cname, true).getBlockBlobReference(bname);
        blob.uploadFromFile(fname);
        String fqname = fqBlobname(cname, bname);
        logger.debug(String.format("uploadFromFile - ok - file %s -> %s", fname, fqname));
    }
    
    public void uploadFromString(String cname, String bname, String content) throws Exception {

    	if (content != null) {
    		byte[] bytes = content.getBytes();
            CloudBlockBlob blob = getContainer(cname, true).getBlockBlobReference(bname);
            blob.uploadFromByteArray(bytes, 0, bytes.length);
            String fqname = fqBlobname(cname, bname);
            logger.debug(String.format("uploadFromString - ok - length %d -> %s", bytes.length, fqname));
    	}
    }
    
    public String getText(String cname, String bname) throws Exception {

        CloudBlockBlob blob = getContainer(cname, true).getBlockBlobReference(bname);
        return blob.downloadText();
    }
    
    public void downloadToFile(String cname, String bname, String fname) throws Exception {

        CloudBlockBlob blob = getContainer(cname, true).getBlockBlobReference(bname);
        blob.downloadToFile(fname);
        String fqname = fqBlobname(cname, bname);
        logger.debug(String.format("downloadToFile - ok - %s -> file %s", fqname, fname));
    }
    
    public boolean deleteBlob(String cname, String bname) {

    	try {
			CloudBlobContainer container = getContainer(cname, false);
			if (container != null) {
				CloudBlockBlob blob = container.getBlockBlobReference(bname);
			    if (blob != null) {
			    	blob.delete();
			        logger.debug(String.format("deleteBlob - ok - %s", fqBlobname(cname, bname)));
			        return true;
			    }
			    else {
			    	logger.debug(String.format("deleteBlob - blob not found - %s", fqBlobname(cname, bname)));
			    }
			}
			else {
				logger.debug(String.format("deleteBlob - container not found - %s", cname));
			}
		} 
    	catch (Throwable e) {
			//e.printStackTrace();
		}
    	return false;
    }
    
    public void listAllBlobs(String containerPrefix) {

        for (CloudBlobContainer container : blobClient.listContainers(containerPrefix)) {
        	String cname = container.getName();
            for (ListBlobItem blob : container.listBlobs()) {
                if (blob instanceof CloudBlob) {
                	logger.debug(String.format("container: %s -> blob: %s: %s", cname, ((CloudBlob) blob).getProperties().getBlobType(), blob.getUri().toString()));
                }
            }
        }
    }
    
    public void listBlobsInContainer(String cname) {

    	try {
			CloudBlobContainer container = getContainer(cname, false);
			if (container != null) {
	            for (ListBlobItem blob : container.listBlobs()) {
	                if (blob instanceof CloudBlob) {
	                	logger.debug(String.format("container: %s -> blob: %s: %s", cname, ((CloudBlob) blob).getProperties().getBlobType(), blob.getUri().toString()));
	                }
	            }
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
    }
    
    /**
     * Return a fully-qualified blob name, in the format "[cname:bname]".
     */
    private String fqBlobname(String cname, String bname) {
    	
    	return "[" + cname + ":" + bname + "]";
    }
	
}

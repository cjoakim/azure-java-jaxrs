package com.joakim.azure;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * JUnit unit test for class com.joakim.azure.Config
 * 
 * $ mvn -Dtest=DataConfig* test
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/05/31
 */

public class ConfigTest extends TestCase {
	
	private final static Logger logger = Logger.getLogger(ConfigTest.class);

	public ConfigTest(String name) {
		
		super(name);
	}
	
	public void test_determineEnvironment() throws Exception {
		
		String env = Config.determineEnvironment();
		assertEquals(env, "dev");
	}
	
	public void test_propertiesResourceName() throws Exception {
		
		String name = Config.propertiesResourceName();
		assertEquals(name, "dev_data.properties");
	}

	public void test_getConfigVersion() throws Exception {
		
		String vers = Config.getConfigVersion();
		assertEquals(vers, "2016/05/31");
	}
	
	public void test_envVar() throws Exception {
		
		String value = Config.envVar("SHELL");
		assertEquals(value, "/bin/bash");
	}
	
	public void test_storageConnectionString() throws Exception {
		
		String value = Config.storageConnectionString();
		logger.debug("storageConnectionString: " + value);
		assertTrue(value.startsWith("DefaultEndpointsProtocol=https;AccountName=cjoakimstorage;AccountKey="));
	}	
	
}

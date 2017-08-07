package com.joakim.azure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Instances of this class are used to obtain configuration values for the other classes
 * in this project.  The configuration values typically come from environment variables
 * per the "Twelve-Factor App" design; see http://12factor.net.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public class Config extends Object implements EnvVarNames {
	
	// Constants:
	private final static Logger logger = Logger.getLogger(Config.class);
	
	private static final String ENV_DEV  = "dev";
	private static final String ENV_TEST = "test";
	private static final String ENV_PROD = "prod";
	private static final Config INSTANCE = new Config();
	
	// Class variables:
	private static String envName;
	private static String configVersion;
	

	private Config() {

		Properties p = new Properties();
		InputStream inputStream = null;

		try {
			ClassLoader classLoader = Config.class.getClassLoader();
			String resourceName = propertiesResourceName();
			inputStream = classLoader.getResourceAsStream(resourceName);
			p.load(inputStream);

			setConfigVersion(p.getProperty("config_version").trim());
			
			logger.debug("properties loaded");
			
			Map<String, String> env = System.getenv();
	        for (String envName : env.keySet()) {
	            //System.out.format("envvar: %s=%s%n", envName, env.get(envName));
	        }
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		finally {
			try {
				inputStream.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return a String value corresponding to the runtime environment we're currently in -
	 * such as "prod", "dev", etc.
	 */
	public static synchronized String determineEnvironment() {
	
		if (envName == null) {
			// Lazy-initialize the "envName" variable.
			// TODO - implement as necessary, perhaps by reading an environment variable like 'DATA_ENV'.
			envName = ENV_DEV;
		}
		return envName;
	}
	
	/**
	 * Return a String containing the environment-specific properties file to be loaded.
	 */
	public static synchronized String propertiesResourceName() {
		
		return determineEnvironment() + "_data.properties";
	}
	
	public static synchronized Config getInstance() {
		return INSTANCE;
	}

	public static synchronized String getConfigVersion() {
		return configVersion;
	}

	public static synchronized void setConfigVersion(String s) {
		configVersion = s;
	}
	
	public static synchronized Map<String, String> envVars() {
		
		return System.getenv();
	}
	
	public static synchronized String envVar(String name) {
		
		return envVars().get(name);
	}
	
	public static synchronized String storageConnectionString() {
		
		String acctName = envVar(EnvVarNames.AZURE_STORAGE_ACCOUNT);
		String acctKey  = envVar(EnvVarNames.AZURE_STORAGE_ACCESS_KEY);
		logger.debug("acctName: " + acctName);
		logger.debug("acctKey:  " + acctKey);
		return String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s", acctName, acctKey);
	}
	
}

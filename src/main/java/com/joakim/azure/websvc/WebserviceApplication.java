package com.joakim.azure.websvc;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the main JAX-RS web application.
 * 
 * @author  Chris Joakim
 * @version 2016/07/25
 */

public class WebserviceApplication extends Application {
	
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>>  classes = new HashSet<Class<?>>();

   public WebserviceApplication() {

      singletons.add(new PingResourceImpl());
      singletons.add(new RedisResourceImpl());
      //singletons.add(new StorageResourceImpl());
   }

   @Override
   public Set<Class<?>> getClasses() {
	   
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
	   
      return singletons;
   }
}

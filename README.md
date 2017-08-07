# azure-java-jaxrs

This project implements the following:

1) Java utility code to access various Azure functionality such as Azure Redis Cache, etc.
2) A Java web application built with the JAX-RS API, which exposes some of the utility code.

## links

- https://github.com/cjoakim/azure-java-jaxrs (this repo)
- https://azure.microsoft.com/en-us/documentation/articles/web-sites-java-get-started/
- https://azure.microsoft.com/en-us/documentation/articles/web-sites-java-add-app/

- http://resteasy.jboss.org
- https://github.com/resteasy/Resteasy
- https://github.com/resteasy/resteasy-examples
- https://www.mkyong.com/webservices/jax-rs/jersey-hello-world-example/   (2011)

- https://azure.microsoft.com/en-us/
- https://azure.microsoft.com/en-us/develop/java/
- https://azure.microsoft.com/en-us/documentation/services/storage/
- https://azure.microsoft.com/en-us/documentation/services/redis-cache/
- https://azure.microsoft.com/en-us/documentation/services/documentdb/
- https://azure.microsoft.com/en-us/documentation/articles/documentdb-mongodb-mongochef/
- https://azure.microsoft.com/en-us/documentation/articles/storage-java-how-to-use-table-storage/
- https://azure.microsoft.com/en-us/documentation/articles/cache-java-get-started/
- https://azure.microsoft.com/en-us/documentation/articles/service-bus-java-how-to-use-queues/
- https://azure.microsoft.com/en-us/documentation/articles/service-bus-pricing-billing/

## workstation assumptions

- Java 8 is installed
- Apache Maven is installed
- The Jetty Server is installed
- The Tomcat Server is installed
- Python 3 is installed
- various environment variables are set; see below

## resteasy

RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services
and RESTful Java applications.  It is a fully certified and portable implementation of the JAX-RS 2.0
specification, a JCP specification that provides a Java API for RESTful Web Services over the HTTP
protocol.

See http://resteasy.jboss.org

## azure

Create a web application, and configure it to use the Jetty application server per
these instructions:

https://azure.microsoft.com/en-us/documentation/articles/web-sites-java-get-started/

## maven

Apache Maven is used as the build tool for this project.  See files 'pom.xml' and 'build.sh'.


## building and deploying the app

This project assumes that you have both the Jetty and Tomcat servers installed
locally, and have exported the JETTY_HOME and TOMCAT_HOME environment variables.

Modify entry DEPLOYMENT_WAR_FILE in build.sh as necessary for your workstation.
This entry is used to deploy the web application to Azure from a separate 
GitHub deployment repo containing *only* the war file; not the source code.

```
./build.sh
```

## bulk load the zip code data into azure redis cache

The following script will read the csv data file, and populate the azure redis cache
with one key per zip code.  The cached data is in JSON format, while the key is a
zip code String value like "28036".

This bulk-loading utility uses the Redis client code, rather than the JAX-RS endpoints
implemented in this simple web app.


```
./redis_zip_codes.sh
```

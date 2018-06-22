FROM        tomcat:8.0.21-jre8
MAINTAINER  Chris Joakim
COPY        ./target/jaxrs.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]


# Example Docker commands:
# docker build -t cjoakim/azure-java-jaxrs . 
# docker run --env-file ./docker-env.txt -it --rm -p 8888:8080 cjoakim/azure-java-jaxrs:latest 
#
# docker run -d -p 3000:3000 cjoakim/azure-java-jaxrs:latest
# docker run -d -p 80:3000 cjoakim/azure-java-jaxrs:latest
# docker run -e MONGODB_URI=$MONGODB_AZURE_URI -d -p 80:3000 cjoakim/azure-java-jaxrs:latest 
# docker ps
# docker stop -t 2 86b125ed43e5  (where 86b125ed43e5 is the CONTAINER ID from 'docker ps')
# docker push cjoakim/azure-java-jaxrs:latest

# docker run -it --rm -p 8888:8080 cjoakim/azure-java-jaxrs:latest

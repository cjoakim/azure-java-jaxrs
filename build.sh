#!/bin/bash

# Compile this app, build war file, and create Docker image.
# Chris Joakim, Microsoft, 2018/06/22

mvn clean compile package war:war

mv target/azure-java-jaxrs.war target/jaxrs.war

docker build -t cjoakim/azure-java-jaxrs .

echo 'done'

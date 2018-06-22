#!/bin/bash

# This script calculates the maven CLASSPATH and produces the
# 'classpath' file, which can be sourced from a bash shell script.
#
# Chris Joakim, Microsoft, 2018/06/22

# Note: AZURE_DEPLOYMENT_WAR_FILE is in a separate directory and GitHub repo!
AZURE_DEPLOYMENT_WAR_FILE=/Users/cjoakim/github/azure-java-jaxrs-deployment/webapps/ROOT.WAR
JETTY_DEPLOY_DIR=$JETTY_HOME/webapps
TOMCAT_DEPLOY_DIR=$TOMCAT_HOME/webapps

mvn clean compile package war:war

source classpath.sh

echo 'copying war file to sibling repo: '$AZURE_DEPLOYMENT_WAR_FILE
cp target/azure-java-jaxrs.war $AZURE_DEPLOYMENT_WAR_FILE

jar tvf $AZURE_DEPLOYMENT_WAR_FILE > tmp/war_contents.txt

# echo 'deploying war file to '$JETTY_DEPLOY_DIR
# cp $AZURE_DEPLOYMENT_WAR_FILE $JETTY_DEPLOY_DIR

echo 'deploying war file to '$TOMCAT_DEPLOY_DIR
cp $AZURE_DEPLOYMENT_WAR_FILE $TOMCAT_DEPLOY_DIR

echo 'listing contents of $TOMCAT_DEPLOY_DIR:'
ls -al $TOMCAT_DEPLOY_DIR

echo 'done'

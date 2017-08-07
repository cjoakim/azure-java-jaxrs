#!/bin/bash

# This script calculates the maven CLASSPATH and produces the
# 'classpath' file, which can be sourced from a bash shell script.
#
# Chris Joakim, Microsoft, 2017/08/07

# Note: DEPLOYMENT_WAR_FILE is in a separate directory and GitHub repo!
DEPLOYMENT_WAR_FILE=/Users/cjoakim/github/azure-java-jaxrs-deployment/webapps/ROOT.WAR
JETTY_DEPLOY_DIR=$JETTY_HOME/webapps
TOMCAT_DEPLOY_DIR=$TOMCAT_HOME/webapps

mvn clean compile package war:war

source classpath.sh

echo 'copying war file to sibling repo: '$DEPLOYMENT_WAR_FILE
cp target/azure-java-jaxrs.war $DEPLOYMENT_WAR_FILE

jar tvf $DEPLOYMENT_WAR_FILE > tmp/war_contents.txt

# echo 'deploying war file to '$JETTY_DEPLOY_DIR
# cp $DEPLOYMENT_WAR_FILE $JETTY_DEPLOY_DIR

# echo 'deploying war file to '$TOMCAT_DEPLOY_DIR
# cp $DEPLOYMENT_WAR_FILE $TOMCAT_DEPLOY_DIR

echo 'done'

#!/bin/bash

# Bash shell script to receive events to the Azure EventHub.
# Chris Joakim, Microsoft, 2016/08/03

source classpath

echo 'EventHubProcessorMain ...'
java -classpath $CP com.joakim.azure.eventhub.EventHubProcessorMain > tmp/evthub_receive.log

echo 'done'

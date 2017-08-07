#!/bin/bash

# Bash shell script to generate and send randomized events to the Azure EventHub.
# Chris Joakim, Microsoft, 2016/08/03

source classpath

echo '--event-hub-gen-events ...'
java -classpath $CP com.joakim.azure.Main --event-hub-gen-events --instances 10 --max-events 1000 > tmp/evthub_send.log

echo 'done'

#!/bin/bash

# This bash shell script is for ad-hoc use on the Azure functionality currently
# being developed in this project.
#
# Chris Joakim, Microsoft, 2016/08/03

source classpath

# echo 'blob ...'
# java -classpath $CP com.joakim.azure.Main --blob

# echo 'redis-get ...'
# java -classpath $CP com.joakim.azure.Main --redis-get --key 28909
# java -classpath $CP com.joakim.azure.Main --redis-get --key 28909bad

# echo '--event-hub-gen-events ...'
# java -classpath $CP com.joakim.azure.Main --event-hub-gen-events --instances 3 --max-events 9

# echo '--event-hub-gen-events ...'
# java -classpath $CP com.joakim.azure.Main --event-hub-process-events --max-events 9

echo 'EventHubProcessorMain ...'
java -classpath $CP com.joakim.azure.eventhub.EventHubProcessorMain

echo 'done'

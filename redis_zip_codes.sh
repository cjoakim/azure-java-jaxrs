#!/bin/bash

# This bash shell script is used to populate the Azure Redis Cache with the 
# contents of the data/postal_codes_nc.csv file.  The Redis key is the zip code.
# and the corresponding value is a JSON document created from the csv line.
# This script will also fetch, for verification purposes, the populated keys.
#
# Chris Joakim, Microsoft, 2016/07/25

source classpath

echo 'redis load ...'
java -classpath $CP com.joakim.azure.Main --redis --load-cache-zipcodedata &> tmp/redis_load.log

echo 'redis read ...'
java -classpath $CP com.joakim.azure.Main --redis --read-cache-zipcodedata &> tmp/redis_read.log

echo 'scanning load log for Davidson ...'
cat tmp/redis_load.log | grep Davidson

echo 'scanning read log for Davidson ...'
cat tmp/redis_read.log | grep Davidson

echo 'done'

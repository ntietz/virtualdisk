#!/bin/bash

client="localhost 9100"

echo "Starting client with arguments \"$client\""
java -jar dist/sample.jar client $client > log.client &


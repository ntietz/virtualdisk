#!/bin/bash

client="localhost 9100 dist/sample.jar"

echo "Starting client with arguments \"$client\""
java -jar dist/sample.jar client $client


#!/bin/bash

#coordinator="9100 localhost 9000 localhost 9001 localhost 9002 localhost 9003 localhost 9004 localhost 9005 localhost 9006 localhost 9007 localhost 9008 localhost 9009"
coordinator="9100 localhost 9000 localhost 9001 localhost 9002 localhost 9003 localhost 9004"

echo "Starting coordinator with arguments \"$coordinator\""
java -jar dist/sample.jar coordinator $coordinator > log.coordinator &


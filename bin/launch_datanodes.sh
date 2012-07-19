#!/bin/bash

node[0]="9000 1024 node0.dat 2048"
node[1]="9001 1024 node1.dat 2048"
node[2]="9002 1024 node2.dat 2048"
node[3]="9003 1024 node3.dat 2048"
node[4]="9004 1024 node4.dat 2048"
#node[5]="9005 1024 node5.dat 2048"
#node[6]="9006 1024 node6.dat 2048"
#node[7]="9007 1024 node7.dat 2048"
#node[8]="9008 1024 node8.dat 2048"
#node[9]="9009 1024 node9.dat 2048"

#for index in {0..9}
for index in {0..4}
do
    echo "Starting datanode with arguments \"${node[$index]}\""
    java -jar dist/sample.jar datanode ${node[$index]} > log.$index &
done


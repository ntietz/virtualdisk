#!/bin/bash

node[0]="9000 node0.dat 2048"
node[1]="9001 node1.dat 2048"
node[2]="9002 node2.dat 2048"
node[3]="9003 node3.dat 2048"
node[4]="9004 node4.dat 2048"
node[5]="9005 node5.dat 2048"
node[6]="9006 node6.dat 2048"
node[7]="9007 node7.dat 2048"
node[8]="9008 node8.dat 2048"
node[9]="9009 node9.dat 2048"

for index in {0..9}
do
    echo "Starting datanode with arguments \"${node[$index]}\""
    java -jar dist/sample.jar datanode ${node[$index]} > log.$index &
done


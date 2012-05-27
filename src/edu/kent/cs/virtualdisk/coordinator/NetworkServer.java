package edu.kent.cs.virtualdisk.coordinator;

import edu.kent.cs.virtualdisk.network.*;

import java.util.*;

public interface NetworkServer
{

    /*
     * This method should issue an order request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract Integer issueOrderRequest(SegmentGroup targets, Integer volumeId, Integer logicalOffset, Date timestamp);

    /*
     * This method should issue a write request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract Integer issueWriteRequest(SegmentGroup targets, Integer volumeId, Integer logicalOffset, byte[] block, Date timestamp);

    /*
     * This method should issue a read request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract Integer issueReadRequest(SegmentGroup targets, Integer volumeId, Integer logicalOffset);

    /*
     * This method should return a list of all the results (in progress or complete) from the order request.
     * This method must be implemented.
     */
    public abstract List<OrderRequestResult> getOrderRequestResults(Integer requestId);

    /*
     * This method should return a list of all the results (in progress or complete) from the write request.
     * This method must be implemented.
     */
    public abstract List<WriteRequestResult> getWriteRequestResults(Integer requestId);

    /*
     * This method should return a list of all the results (in progress or complete) from the read request.
     * This method must be implemented.
     */
    public abstract List<ReadRequestResult> getReadRequestResults(Integer requestId);

    /*
     * This method should return a list of the datanodes the network server is configured to communicate with;
     * This method must be implemented.
     */
    public abstract List<DataNodeIdentifier> getDataNodes();

    /*
     * This method attaches a new datanode to the server.
     * This method must be implemented.
     */
    public abstract boolean attachDataNode(DataNodeIdentifier node);

    /*
     * This method detaches a datanode from the server.
     * This method must be implemented.
     */
    public abstract boolean detachDataNode(DataNodeIdentifier node);

}


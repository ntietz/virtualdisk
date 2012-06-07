package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.request.CreateVolumeRequestResult;
import com.virtualdisk.network.request.DeleteVolumeRequestResult;
import com.virtualdisk.network.request.OrderRequestResult;
import com.virtualdisk.network.request.ReadRequestResult;
import com.virtualdisk.network.request.VolumeExistsRequestResult;
import com.virtualdisk.network.request.WriteRequestResult;

import java.util.*;

public abstract class NetworkServer
{

    /*
     * This method should issue an order request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract int issueOrderRequest(SegmentGroup targets, int volumeId, long logicalOffset, Date timestamp);
    
    /*
     * This method should issue a write request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract int issueWriteRequest(SegmentGroup targets, int volumeId, long logicalOffset, byte[] block, Date timestamp);

    /*
     * This method should issue a read request to the nodes/node-servers and return the identifier for the request.
     * This method must be implemented.
     */
    public abstract int issueReadRequest(SegmentGroup targets, int volumeId, long logicalOffset);

    public abstract int issueVolumeCreationRequest(int volumeId);
    
    public abstract int issueVolumeDeletionRequest(int volumeId);
    
    public abstract int issueVolumeExistsRequest(int volumeId);
    
    /*
     * This method should return a list of all the results (in progress or complete) from the order request.
     * This method must be implemented.
     */
    public abstract List<OrderRequestResult> getOrderRequestResults(int requestId);

    /*
     * This method should return a list of all the results (in progress or complete) from the write request.
     * This method must be implemented.
     */
    public abstract List<WriteRequestResult> getWriteRequestResults(int requestId);

    /*
     * This method should return a list of all the results (in progress or complete) from the read request.
     * This method must be implemented.
     */
    public abstract List<ReadRequestResult> getReadRequestResults(int requestId);

    public abstract List<CreateVolumeRequestResult> getVolumeCreationRequestResults(int requestId);
    
    public abstract List<DeleteVolumeRequestResult> getVolumeDeletionRequestResults(int requestId);
    
    public abstract List<VolumeExistsRequestResult> getVolumeExistsRequestResults(int requestId);

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


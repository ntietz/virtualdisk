package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;
import com.virtualdisk.network.request.WriteRequestResult;

import org.jboss.netty.channel.*;

import java.util.*;

/**
 * An abstract class which defines the API of the network server.
 * This allows a decoupling of the logic of the coordinator and datanode and the mechanics of transporting the data.
 */
public abstract class NetworkServer
{
    /**
     * The number of milliseconds which must elapse from sending before a request is considered timed out.
     */
    private static long timeoutLength = 1000;

    /**
     * Sets the length (in milliseconds) from sending before a request is considered timed out.
     * @param   timeoutLength   the length in milliseconds
     */
    public static void setTimeoutLength(long timeoutLength)
    {
        NetworkServer.timeoutLength = timeoutLength;
    }

    /**
     * A getter for the timeout length.
     * @return  the length of the timeout in milliseconds
     */
    public static long timeoutLength()
    {
        return timeoutLength;
    }

    public abstract DataNodeIdentifier getNodeFromChannel(Channel channel);

    /**
     * This method should issue an order request to the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   targets         the targets which the order request is issued to
     * @param   volumeId        the volume id to order on
     * @param   logicalOffset   the logical location to order on
     * @param   timestamp       the timestamp of the request
     * @return  the identifier of the request
     */
    public abstract int issueOrderRequest(SegmentGroup targets, int volumeId, long logicalOffset, Date timestamp);
    
    /**
     * This method should issue a write request to the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   targets         the targets which the write request is issued to
     * @param   volumeId        the volume id to write to
     * @param   logicalOffset   the logical location to write to
     * @param   block           the data to write
     * @param   timestamp       the timestamp of the request
     * @return  the identifier of the request
     */
    public abstract int issueWriteRequest(SegmentGroup targets, int volumeId, long logicalOffset, byte[] block, Date timestamp);

    /**
     * This method should issue a read request to the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   targets         the targets which the write request is issued to
     * @param   volumeId        the volume id to write to
     * @param   logicalOffset   the logical location to write to
     * @return  the identifier of the request
     */
    public abstract int issueReadRequest(SegmentGroup targets, int volumeId, long logicalOffset);

    /**
     * This method should issue a volume creation request to all the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   volumeId    the volume to create
     * @return  the identifier of the request
     */
    public abstract int issueVolumeCreationRequest(int volumeId);
    
    /**
     * This method should issue a volume deletion request to all the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   volumeId    the volume to delete
     * @return  the identifier of the request
     */
    public abstract int issueVolumeDeletionRequest(int volumeId);
    
    /**
     * This method should issue a volume exists query to all the datanodes and return the identifier of the request.
     * This method must be implemented.
     * @param   volumeId    the volume to check for
     * @return  the identifier of the request
     */
    public abstract int issueVolumeExistsRequest(int volumeId);

    public abstract int issueUnsetSegmentRequest(List<DataNodeIdentifier> targets, int volumeId, long startingOffset, long stoppingOffset);
    
    /**
     * This method should return a list of all the results (in progress or complete) from the order request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<OrderRequestResult> getOrderRequestResults(int requestId);

    /**
     * This method should return a list of all the results (in progress or complete) from the write request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<WriteRequestResult> getWriteRequestResults(int requestId);

    /**
     * This method should return a list of all the results (in progress or complete) from the read request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<ReadRequestResult> getReadRequestResults(int requestId);

    /**
     * This method should return a list of all the results (in progress or complete) from the volume create request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<CreateVolumeRequestResult> getVolumeCreationRequestResults(int requestId);
    
    /**
     * This method should return a list of all the results (in progress or complete) from the volume delete request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<DeleteVolumeRequestResult> getVolumeDeletionRequestResults(int requestId);
    
    /**
     * This method should return a list of all the results (in progress or complete) from the volume-exists request.
     * This method must be implemented.
     * @param   requestId   the request to get the results of
     * @return  the results of the request
     */
    public abstract List<VolumeExistsRequestResult> getVolumeExistsRequestResults(int requestId);

    public abstract List<UnsetSegmentRequestResult> getUnsetSegmentRequestResults(int requestId);

    /**
     * Sets the future results for a request.
     * @param   requestId   the request's id
     * @param   futures     the list of futures
     */
    public abstract void setResultFutures(int requestId, List<RequestFuture> futures);

    /**
     * Gets the future results from a request
     * @param   requestId   the request's id
     * @return  the futures of the request
     */
    public abstract List<RequestFuture> getResultFutures(int requestId);

    /**
     * This method should return a list of the datanodes the network server is configured to communicate with.
     * This method must be implemented.
     * @return  the list of all ids for all datanodes attached to the server
     */
    public abstract List<DataNodeIdentifier> getDataNodes();

    /**
     * This method attaches (and connects to) a new datanode to the server.
     * This method must be implemented.
     * @param   node    the datanode to try to attach
     * @return  true if attaching succeeds, false otherwise
     */
    public abstract boolean attachDataNode(DataNodeIdentifier node);

    /**
     * This method detaches a datanode from the server.
     * This method must be implemented.
     * @param   node    the datanode to try to detach
     * @return  true if detaching succeeds, false otherwise
     */
    public abstract boolean detachDataNode(DataNodeIdentifier node);

}


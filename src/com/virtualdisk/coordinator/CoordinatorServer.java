package com.virtualdisk.coordinator;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The primary implementation of the NetworkServer api.
 * This class is a singleton.
 */
public class CoordinatorServer
extends NetworkServer
{
    /**
     * An instance of this server.
     */
    private static CoordinatorServer instance;

    /**
     * The last assigned request id, used to assign new unique request ids.
     * All request ids are assigned sequentially and are not repeated (for almost 2^32 requests)
     */
    private int lastAssignedId;

    /**
     * A list of identifiers for all the connected nodes.
     */
    private List<DataNodeIdentifier> allNodes;
    
    /**
     * A map of datanode ids to their channels.
     * protected (not private) so SingletonCordinator can access it.
     */
    private Map<Integer, Channel> channelMap;
    
    /**
     * A map of request ids to their result-futures, which are used to handle waiting for results.
     * protected (not private) so SingletonCordinator can access it.
     */
    private Map<Integer, List<RequestFuture>> resultMap;

    /**
     * Private constructor to preserve singleton property.
     * @param   allNodes    the list of all nodes the coordinator is initially connected to
     * @param   channelMap  the map of datanode id to channel for the initially connected nodes
     */
    private CoordinatorServer( List<DataNodeIdentifier> allNodes
                             , Map<Integer, Channel> channelMap
                             )
    {
        lastAssignedId = 0;

        this.allNodes = allNodes;
        this.channelMap = channelMap;
        resultMap = new ConcurrentHashMap<Integer, List<RequestFuture>>();

        instance = this;
    }

    /**
     * Gets an instances of the server, and constructs it if it does not yet exist.
     * @param   allNodes    all the nodes which the coordinator is initially connected to
     * @param   channelMap  a mapping of datanode id to the channel for the node
     */
    public static CoordinatorServer getInstance( List<DataNodeIdentifier> allNodes
                                               , Map<Integer, Channel> channelMap
                                               )
    {
        if (instance == null)
        {
            instance = new CoordinatorServer(allNodes, channelMap);
        }
        return instance;
    }

    /**
     * This method creates and issues an order request, and sets up the handlers for the return of the result.
     * @param   targets         the nodes to perform the order request on
     * @param   volumeId        the volume to order on
     * @param   logicalOffset   the logical location to order
     * @param   timestamp       the timestamp of the request
     * @return  the id of the request
     */
    public int issueOrderRequest( SegmentGroup targets
                                , int volumeId
                                , long logicalOffset
                                , Date timestamp
                                )
    {
        int id = generateNewRequestId();

        OrderRequest request = new OrderRequest(id, volumeId, logicalOffset, timestamp);

        List<RequestFuture> futures = Collections.synchronizedList(new ArrayList<RequestFuture>());
        for (int index = 0; index < targets.getMembers().size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets.getMembers())
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    /**
     * This method creates and issues a write request, and sets up the handlers for the return of the result.
     * @param   targets         the nodes to perform the write request on
     * @param   volumeId        the volume to write to
     * @param   logicalOffset   the logical location to write to
     * @param   block           the data to write
     * @param   timestamp       the timestamp of the request
     * @return  the id of the request
     */
    public int issueWriteRequest( SegmentGroup targets
                                , int volumeId
                                , long logicalOffset
                                , byte[] block
                                , Date timestamp
                                )
    {
        int id = generateNewRequestId();

        WriteRequest request = new WriteRequest(id, volumeId, logicalOffset, timestamp, block);

        List<RequestFuture> futures = new ArrayList<RequestFuture>();
        for (int index = 0; index < targets.getMembers().size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets.getMembers())
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    /**
     * This method creates and issues a read request, and sets up the handlers for the return of the result.
     * @param   targets         the nodes to read from
     * @param   volumeId        the volume to order on
     * @param   logicalOffset   the logical location to order
     * @return  the id of the request
     */
    public int issueReadRequest( SegmentGroup targets
                               , int volumeId
                               , long logicalOffset
                               )
    {
        int id = generateNewRequestId();

        ReadRequest request = new ReadRequest(id, volumeId, logicalOffset);

        List<RequestFuture> futures = new ArrayList<RequestFuture>();
        for (int index = 0; index < targets.getMembers().size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets.getMembers())
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    /**
     * This method creates and issues a volume creation request, and sets up the handlers for the return of the result.
     * @param   volumeId        the volume to create
     * @return  the id of the request
     */
    public int issueVolumeCreationRequest(int volumeId)
    {
        List<DataNodeIdentifier> targets = allNodes;
        int id = generateNewRequestId();

        CreateVolumeRequest request = new CreateVolumeRequest(id, volumeId);

        List<RequestFuture> futures = new ArrayList<RequestFuture>();
        for (int index = 0; index < targets.size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets)
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    /**
     * This method creates and issues a volume deleition request, and sets up the handlers for the return of the result.
     * @param   volumeId        the volume to delete
     * @return  the id of the request
     */
    public int issueVolumeDeletionRequest(int volumeId)
    {
        List<DataNodeIdentifier> targets = allNodes;
        int id = generateNewRequestId();

        DeleteVolumeRequest request = new DeleteVolumeRequest(id, volumeId);

        List<RequestFuture> futures = new ArrayList<RequestFuture>();
        for (int index = 0; index < targets.size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets)
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;

    }

    /**
     * This method creates and issues a volume existence request, and sets up the handlers for the return of the result.
     * @param   volumeId        the volume to check for
     * @return  the id of the request
     */
    public int issueVolumeExistsRequest(int volumeId)
    {
        List<DataNodeIdentifier> targets = allNodes;
        int id = generateNewRequestId();

        VolumeExistsRequest request = new VolumeExistsRequest(id, volumeId);

        List<RequestFuture> futures = new ArrayList<RequestFuture>();
        for (int index = 0; index < targets.size(); ++index)
        {
            RequestFuture future = new RequestFuture(id, System.currentTimeMillis(), request.messageType());
            futures.add(future);
        }

        resultMap.put(id, futures);

        for (DataNodeIdentifier eachTarget : targets)
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    public int issueUnsetSegmentRequest(List<DataNodeIdentifier> targets, int volumeId, long startingOffset, long stoppingOffset)
    {
        int id = generateNewRequestId();

        UnsetSegmentRequest request = new UnsetSegmentRequest(id, volumeId, startingOffset, stoppingOffset);

        for (DataNodeIdentifier eachTarget : targets)
        {
            int targetId = eachTarget.getNodeId();
            Channel channel = channelMap.get(targetId);
            channel.write(request);
        }

        return id;
    }

    /**
     * Returns all the order request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<OrderRequestResult> getOrderRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<OrderRequestResult>();
        }
        else
        {
            List<OrderRequestResult> results = new ArrayList<OrderRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((OrderRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Returns all the write request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<WriteRequestResult> getWriteRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<WriteRequestResult>();
        }
        else
        {
            List<WriteRequestResult> results = new ArrayList<WriteRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((WriteRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Returns all the read request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<ReadRequestResult> getReadRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<ReadRequestResult>();
        }
        else
        {
            List<ReadRequestResult> results = new ArrayList<ReadRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((ReadRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Returns all the volume creation request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<CreateVolumeRequestResult> getVolumeCreationRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<CreateVolumeRequestResult>();
        }
        else
        {
            List<CreateVolumeRequestResult> results = new ArrayList<CreateVolumeRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((CreateVolumeRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Returns all the volume deletion request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<DeleteVolumeRequestResult> getVolumeDeletionRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<DeleteVolumeRequestResult>();
        }
        else
        {
            List<DeleteVolumeRequestResult> results = new ArrayList<DeleteVolumeRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((DeleteVolumeRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Returns all the volume exists request results for the supplied request id.
     * This method converts the request futures into request results.
     * @param   requestId   the id for the request we want the results of
     * @return  the list of all results of the request
     */
    public List<VolumeExistsRequestResult> getVolumeExistsRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);

        if (futures == null)
        {
            return new ArrayList<VolumeExistsRequestResult>();
        }
        else
        {
            List<VolumeExistsRequestResult> results = new ArrayList<VolumeExistsRequestResult>();

            for (RequestFuture each : futures)
            {
                results.add((VolumeExistsRequestResult)each.getResult());
            }

            return results;
        }
    }

    public List<UnsetSegmentRequestResult> getUnsetSegmentRequestResults(int requestId)
    {
        List<RequestFuture> futures = resultMap.get(requestId);
        List<UnsetSegmentRequestResult> results = new ArrayList<UnsetSegmentRequestResult>();

        if (futures == null)
        {
            return results;
        }
        else
        {
            for (RequestFuture each : futures)
            {
                results.add((UnsetSegmentRequestResult)each.getResult());
            }

            return results;
        }
    }

    /**
     * Sets the result futures.
     * @param   requestId   the request's id
     * @param   futures     the futures
     */
    public void setResultFutures(int requestId, List<RequestFuture> futures)
    {
        resultMap.put(requestId, futures);
    }

    /**
     * Gets the result futures.
     * @param   requestId   the request's id
     * @return  the futures
     */
    public List<RequestFuture> getResultFutures(int requestId)
    {
        return resultMap.get(requestId);
    }

    /**
     * Fetches a copy of the datanode-identifier list.
     * @return  a copy of the list of connected datanodes
     */
    public List<DataNodeIdentifier> getDataNodes()
    {
        return new ArrayList<DataNodeIdentifier>(allNodes);
    }

    /**
     * Attaches a new datanode to this coordinator.
     * @return  whether or not the attachment succeeded
     */
    public boolean attachDataNode(DataNodeIdentifier node, Channel channel)
    {
        int datanodeId = node.getNodeId();

        allNodes.add(node);
        channelMap.put(datanodeId, channel);

        return true;
    }

    /**
     * Detaches a datanode from this coordinator.
     * @return  whether or not the deattachment succeeded
     */
    public boolean detachDataNode(DataNodeIdentifier node)
    {
        int datanodeId = node.getNodeId();

        allNodes.remove(node);
        channelMap.remove(datanodeId);

        return true;
    }

    /**
     * Generates a new unique id.
     * @return  a new unique request id
     */
    protected synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }
}

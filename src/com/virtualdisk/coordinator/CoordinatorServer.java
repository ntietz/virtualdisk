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

public class CoordinatorServer
extends NetworkServer
{
    private static CoordinatorServer instance;

    private int lastAssignedId;

    private List<DataNodeIdentifier> allNodes;
    private Map<Integer, Channel> channelMap;
    private Map<Integer, List<RequestFuture>> resultMap;

    private CoordinatorServer()
    {
        lastAssignedId = 0;

        allNodes = new ArrayList<DataNodeIdentifier>();
        channelMap = new HashMap<Integer, Channel>();
        resultMap = new ConcurrentHashMap<Integer, List<RequestFuture>>();

        instance = this;
    }

    public static CoordinatorServer getInstance()
    {
        if (instance == null)
        {
            instance = new CoordinatorServer();
        }
        return instance;
    }

    public int issueOrderRequest( SegmentGroup targets
                                , int volumeId
                                , long logicalOffset
                                , Date timestamp
                                )
    {
        int id = generateNewRequestId();

        OrderRequest request = new OrderRequest(id, volumeId, logicalOffset, timestamp);

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

    public List<DataNodeIdentifier> getDataNodes()
    {
        return new ArrayList<DataNodeIdentifier>(allNodes);
    }

    public boolean attachDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    public boolean detachDataNode(DataNodeIdentifier node)
    {
        return false;
    }

    protected synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }

}


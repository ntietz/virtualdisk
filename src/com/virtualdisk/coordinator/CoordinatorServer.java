package com.virtualdisk.coordinator;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

public class CoordinatorServer
extends NetworkServer
{
    private static ChannelGroup allChannels;
    private static Coordinator coordinator;
    private static CoordinatorServer instance;
    private static int lastAssignedId;

    private CoordinatorServer()
    {
        allChannels = new DefaultChannelGroup("CoordinatorServer");
        //coordinator = new Coordinator(0, 0, 0, 0, null, null);

        lastAssignedId = 0;

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

    public static Coordinator getCoordinator()
    {
        if (instance == null)
        {
            instance = new CoordinatorServer();
        }
        return coordinator;
    }

    public int issueOrderRequest(SegmentGroup targets, int volumeId, long logicalOffset, Date timestamp)
    {
        int id = generateNewRequestId();

        OrderRequest request = new OrderRequest(id, volumeId, logicalOffset, timestamp);

        /* TODO
            for (each target)
            {
                write each to the network
                add a Future to the request store
                    (as results come back, they get written into the Future)
            }
        */

        return id;
    }

    public int issueWriteRequest( SegmentGroup targets
                                , int volumeId
                                , long logicalOffset
                                , byte[] block
                                , Date timestamp
                                )
    {
        return 0;
    }

    public int issueReadRequest(SegmentGroup targets, int volumeId, long logicalOffset)
    {
        return 0;
    }

    public int issueVolumeCreationRequest(int volumeId)
    {
        return 0;
    }

    public int issueVolumeDeletionRequest(int volumeId)
    {
        return 0;
    }

    public int issueVolumeExistsRequest(int volumeId)
    {
        return 0;
    }

    public List<OrderRequestResult> getOrderRequestResults(int requestId)
    {
        return null;
    }

    public List<WriteRequestResult> getWriteRequestResults(int requestId)
    {
        return null;
    }

    public List<ReadRequestResult> getReadRequestResults(int requestId)
    {
        return null;
    }

    public List<CreateVolumeRequestResult> getVolumeCreationRequestResults(int requestId)
    {
        return null;
    }

    public List<DeleteVolumeRequestResult> getVolumeDeletionRequestResults(int requestId)
    {
        return null;
    }

    public List<VolumeExistsRequestResult> getVolumeExistsRequestResults(int requestId)
    {
        return null;
    }

    public List<DataNodeIdentifier> getDataNodes()
    {
        return null;
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


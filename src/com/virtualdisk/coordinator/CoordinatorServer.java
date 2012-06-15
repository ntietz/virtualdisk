package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

public class CoordinatorServer
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

        // TODO write it to the network

        return id;
    }

    protected synchronized int generateNewRequestId()
    {
        ++lastAssignedId;
        return lastAssignedId;
    }

}


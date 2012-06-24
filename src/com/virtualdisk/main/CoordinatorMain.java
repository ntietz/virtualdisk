package com.virtualdisk.main;

import com.virtualdisk.network.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.util.*;
import java.util.concurrent.*;

public class CoordinatorMain
{
    private static final int DEFAULT_BLOCK_SIZE = 1024;
    private static final int DEFAULT_SEGMENT_SIZE = 64;
    private static final int DEFAULT_SEGMENT_GROUP_SIZE = 3;
    private static final int DEFAULT_QUORUM_SIZE = 2;

    private static int blockSize = DEFAULT_BLOCK_SIZE;
    private static int segmentSize = DEFAULT_SEGMENT_SIZE;
    private static int segmentGroupSize = DEFAULT_SEGMENT_GROUP_SIZE;
    private static int quorumSize = DEFAULT_QUORUM_SIZE;

    public static void main(String... args)
    {
        // TODO determine usage, convert to Apache Commons handling
        // TODO verify arguments
        /* for now, use the following:
            args
                0   ->  port
                1   ->  datanode0 hostname
                2   ->  datanode0 port
                3   ->  datanode1 hostname
                4   ->  datanode1 port
                ...
               2n-1 ->  datanoden hostname
                2n  ->  datanoden port
        */

        int port = Integer.valueOf(args[0]);

        List<DataNodeIdentifier> nodes = new ArrayList<DataNodeIdentifier>();
        // TODO construct the datanode identifiers

        start(port, nodes);
    }

    public static void start( int port
                            , List<DataNodeIdentifier> nodes
                            )
    {

        ChannelFactory channelFactory = new NioClientSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);

        bootstrap.setPipelineFactory(new CoordinatorPipelineFactory());
    }
}


package com.virtualdisk.main;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class CoordinatorMain
{
    private static final int DEFAULT_BLOCK_SIZE = 1024;
    private static final int DEFAULT_SEGMENT_SIZE = 64;
    private static final int DEFAULT_SEGMENTS_PER_SEGMENTGROUP = 10;
    private static final int DEFAULT_NODES_PER_SEGMENTGROUP = 3;
    private static final int DEFAULT_QUORUM_SIZE = 2;

    private static int blockSize = DEFAULT_BLOCK_SIZE;
    private static int segmentSize = DEFAULT_SEGMENT_SIZE;
    private static int segmentsPerSegmentGroup = DEFAULT_SEGMENTS_PER_SEGMENTGROUP;
    private static int nodesPerSegmentGroup = DEFAULT_NODES_PER_SEGMENTGROUP;
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

        int coordinatorPort = Integer.valueOf(args[0]);

        List<DataNodeIdentifier> nodes = new ArrayList<DataNodeIdentifier>();
        int id = 1;
        for (int index = 1; index < args.length; index += 2)
        {
            String hostname = args[index];
            int port = Integer.valueOf(args[index+1]);
            DataNodeIdentifier identifier = new DataNodeIdentifier(id, hostname, port);
            nodes.add(identifier);
            ++id;
        }

        connectToDataNodes(nodes);
        startClientListener(coordinatorPort);
    }

    public static void connectToDataNodes( List<DataNodeIdentifier> nodes
                                         )
    {

        ChannelFactory channelFactory = new NioClientSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(new CoordinatorPipelineFactory());

        List<DataNodeIdentifier> connectedNodes = new ArrayList<DataNodeIdentifier>();
        Map<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
        for (DataNodeIdentifier node : nodes)
        {
            String host = node.getNodeAddress();
            int port = node.getPort();
            System.out.println("Attempting to connect to " + host + ":" + port + "...");
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
            future.awaitUninterruptibly();
            if (future.isDone() && future.isSuccess())
            {
                connectedNodes.add(node);
                channelMap.put(node.getNodeId(), future.getChannel());
                System.out.println("Connected!");
            }
            else
            {
                System.out.println("Connection failed.");
            }
        }

        SingletonCoordinator.setup( blockSize
                                  , segmentSize
                                  , segmentsPerSegmentGroup
                                  , nodesPerSegmentGroup
                                  , quorumSize
                                  , connectedNodes
                                  , channelMap
                                  );
    }

    public static void startClientListener( int port
                                          )
    {
        ChannelFactory channelFactory = new NioServerSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
        bootstrap.setPipelineFactory(new CoordinatorPipelineFactory());

        bootstrap.bind(new InetSocketAddress(port));
    }
}


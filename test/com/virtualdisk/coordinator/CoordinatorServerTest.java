package com.virtualdisk.coordinator;

import com.virtualdisk.datanode.*;
import com.virtualdisk.main.*;
import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class CoordinatorServerTest
{
    private static final int clusterSize = 10;
    private static final int blockSize = 10;
    private static final int segmentSize = 10;
    private static final int segmentGroupSize = 5;
    private static final int quorumSize = 3;
    private static final int port = 8000;
    private static List<DataNodeIdentifier> nodes = new ArrayList<DataNodeIdentifier>();
    private static Map<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
    private static CoordinatorServer server;
    private static List<SegmentGroup> segmentGroups = new ArrayList<SegmentGroup>();

    @BeforeClass
    public static void init()
    throws Exception
    {
        ChannelFactory channelFactory = new NioClientSocketChannelFactory
            ( Executors.newCachedThreadPool()
            , Executors.newCachedThreadPool()
            );

        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(new CoordinatorPipelineFactory());

        // set up 10 datanodes
        for (int index = 0; index < 10; ++index)
        {
            int id = index;
            int port = DataNodeMain.DEFAULT_PORT + index;
            String address = "localhost";

            List<String> driveHandles = new ArrayList<String>();
            driveHandles.add("data/drive." + index);

            List<Long> driveSizes = new ArrayList<Long>();
            driveSizes.add(1024L);

            // construct and store the node's id
            DataNodeIdentifier nodeId = new DataNodeIdentifier(index, address, port);
            nodes.add(nodeId);
            
            // construct and run the datanode
            DataNodeMain main = new DataNodeMain(port, driveHandles, driveSizes);
            main.start();
        }

        // gives the nodes time to come up
        Thread.sleep(100);

        // connect to each datanode, storing the channel
        for (DataNodeIdentifier node : nodes)
        {
            String address = node.getNodeAddress();
            int port = node.getPort();
            int id = node.getNodeId();

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(address, port));
            future.awaitUninterruptibly();
            //assertNotNull("Channel should not be null.", future.getChannel());
            if (future.getChannel() == null)
            {
                System.out.println("Failed to connect to " + node.getNodeId());
                nodes.remove(node);
            }
            channelMap.put(id, future.getChannel());
        }

        SingletonCoordinator.setup( blockSize
                                  , segmentSize
                                  , segmentGroupSize
                                  , quorumSize
                                  , nodes
                                  , channelMap
                                  );

        server = (CoordinatorServer) SingletonCoordinator.getServer();

        // build some segment groups to use
        for (int index = 0; index < 5; ++index)
        {
            List<DataNodeIdentifier> members = new ArrayList<DataNodeIdentifier>();
            for (int num = index; num < (index + segmentGroupSize)%nodes.size(); ++num)
            {
                members.add(nodes.get(num));
            }

            segmentGroups.add(new SegmentGroup(members));
        }

        server.issueVolumeCreationRequest(0);
        server.issueVolumeCreationRequest(1);
        server.issueVolumeCreationRequest(13);
        server.issueVolumeCreationRequest(14);
    }

    @Test(timeout=10000)
    public void testExists()
    {
        int existsId = server.issueVolumeExistsRequest(13);

        List<VolumeExistsRequestResult> results = server.getVolumeExistsRequestResults(existsId);
        assertEquals("Result list should be right size.", clusterSize, results.size());

        int finished = 0;
        int exists = 0;
        while (finished != clusterSize)
        {
            results = server.getVolumeExistsRequestResults(existsId);
            finished = 0;
            exists = 0;

            for (VolumeExistsRequestResult result : results)
            {
                if (result.isDone())
                {
                    ++finished;
                    if (result.volumeExists())
                    {
                        ++exists;
                    }
                }
            }
        }

        assertEquals("All should finish.", clusterSize, finished);
        assertEquals("All should exist.", clusterSize, exists);
    }

    @Test(timeout=10000)
    public void testOrder()
    {
        SegmentGroup targets = segmentGroups.get(3);

        int orderId = server.issueOrderRequest(targets, 0, 0, new Date());

        List<OrderRequestResult> results = server.getOrderRequestResults(orderId);
        assertEquals("Result list should be right size.", segmentGroupSize, results.size());
        
        int finished = 0;
        int successful = 0;
        while (finished != segmentGroupSize)
        {
            results = server.getOrderRequestResults(orderId);
            finished = 0;
            successful = 0;

            for (OrderRequestResult result : results)
            {
                if (result.isDone())
                {
                    ++finished;
                    if (result.wasSuccessful())
                    {
                        ++successful;
                    }
                }
            }
        }

        assertEquals("All should finish", segmentGroupSize, finished);
        assertEquals("All should succeed", segmentGroupSize, successful);
    }
}


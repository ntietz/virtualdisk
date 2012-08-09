package com.virtualdisk.coordinator;

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

public class CoordinatorIntegrationTest
{
    private static int clusterSize = 10;
    private static int blockSize = 20;
    private static int segmentSize = 15;
    private static int segmentsPerSegmentGroup = 3;
    private static int nodesPerSegmentGroup = 5;
    private static int quorumSize = 3;
    private static List<DataNodeIdentifier> nodes = new ArrayList<DataNodeIdentifier>();
    private static Map<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
    private static Coordinator coordinator;

    private static final byte[] emptyBlock = new byte[blockSize];
    private static final long seed = 2013;

    @BeforeClass
    public static void setupNodes()
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
            DataNodeMain main = new DataNodeMain(port, blockSize, driveHandles, driveSizes);
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
                                  , segmentsPerSegmentGroup
                                  , nodesPerSegmentGroup
                                  , quorumSize
                                  , nodes
                                  , channelMap
                                  , bootstrap
                                  );

        coordinator = SingletonCoordinator.getCoordinator();

        List<Integer> createIds = new ArrayList<Integer>();
        createIds.add(coordinator.createVolume(0));
        createIds.add(coordinator.createVolume(1));
        createIds.add(coordinator.createVolume(13));
        createIds.add(coordinator.createVolume(14));

        for (int each : createIds)
        {
            CreateVolumeRequestResult result = coordinator.createVolumeResult(each);
            while (result == null || !result.isDone())
            {
                result = coordinator.createVolumeResult(each);
            }
        }
    }

    @Test
    public void testConfiguration()
    {
        assertEquals("Block size should match", blockSize, coordinator.getBlockSize());
        assertEquals("Should have right number of datanodes", clusterSize, nodes.size());
        assertEquals("Segment size should match", segmentSize, coordinator.getSegmentSize());
        assertEquals("Segment group size sholud match", nodesPerSegmentGroup, coordinator.getNodesPerSegmentGroup());
        assertEquals("Quorum size should match", quorumSize, coordinator.getQuorumSize());
    }

    @Test(timeout=10000)
    public void testCreateDelete()
    {
        {
            int createId = coordinator.createVolume(15);
            CreateVolumeRequestResult result = null;
            while (result == null || !result.isDone())
            {
                result = coordinator.createVolumeResult(createId);
            }

            assertTrue("Creation should succeed", result.wasSuccessful());
        } {
            int deleteId = coordinator.deleteVolume(15);
            DeleteVolumeRequestResult result = null;
            while (result == null || !result.isDone())
            {
                result = coordinator.deleteVolumeResult(deleteId);
            }

            assertTrue("Deletion should succeed", result.wasSuccessful());
        }
    }

    @Test(timeout=10000)
    public void testWrite()
    {
        {
            final int numberOfWrites = 100;
            Random random = new Random(seed);
            List<Integer> writeIds = new ArrayList<Integer>();

            for (int index = 0; index < numberOfWrites; ++index)
            {
                byte[] block = new byte[blockSize];
                random.nextBytes(block);
                writeIds.add(coordinator.write(1, index, block));
            }

            for (int writeId : writeIds)
            {
                WriteRequestResult result = null;
                while (result == null || !result.isDone())
                {
                    result = coordinator.writeResult(writeId);
                }

                assertTrue("Write should succeed", result.wasSuccessful());
                System.out.println(writeId);
            }
        }
    }

    @Test(timeout=10000)
    public void testWriteRead()
    {
        {
            final int numberOfWrites = 100;
            Random random = new Random(seed);
            List<Integer> writeIds = new ArrayList<Integer>();

            for (int index = 0; index < numberOfWrites; ++index)
            {
                byte[] block = new byte[blockSize];
                random.nextBytes(block);
                writeIds.add(coordinator.write(0, index, block));
            }

            for (int writeId : writeIds)
            {
                WriteRequestResult result = null;
                while (result == null || !result.isDone())
                {
                    result = coordinator.writeResult(writeId);
                }

                assertTrue("Write should succeed", result.wasSuccessful());
            }
        } {
            final int numberOfReads = 100;
            Random random = new Random(seed);
            List<Integer> readIds = new ArrayList<Integer>();

            for (int index = 0; index < numberOfReads; ++index)
            {
                readIds.add(coordinator.read(0, index));
            }

            for (int readId : readIds)
            {
                ReadRequestResult result = null;
                while (result == null || !result.isDone())
                {
                    result = coordinator.readResult(readId);
                }

                byte[] block = new byte[blockSize];
                random.nextBytes(block);

                assertTrue("Read should succeed", result.wasSuccessful());
                assertArrayEquals("Block should match", block, result.getBlock());
            }
        }
    }

    @Test(timeout=10000)
    public void testMixedReadWrite()
    {
        final int numberOfWrites = 100;
        final int numberOfReads = 100;
        Random random = new Random(seed);
        List<Integer> writeIds = new ArrayList<Integer>();
        List<Integer> readIds = new ArrayList<Integer>();

        for (int index = 0; index < numberOfWrites; ++index)
        {
            byte[] block = new byte[blockSize];
            random.nextBytes(block);
            writeIds.add(coordinator.write(0, index, block));
            readIds.add(coordinator.read(0, index));
        }

        for (int writeId : writeIds)
        {
            WriteRequestResult result = null;
            while (result == null || !result.isDone())
            {
                result = coordinator.writeResult(writeId);
            }

            assertTrue("Write should succeed", result.wasSuccessful());
        }

        for (int readId : readIds)
        {
            ReadRequestResult result = null;
            while (result == null || !result.isDone())
            {
                result = coordinator.readResult(readId);
            }
        }
    }

    @Test(timeout=10000)
    public void testReadUnwritten()
    {
        final int numberOfReads = 500;
        List<Integer> readIds = new ArrayList<Integer>();

        for (int index = 0; index < numberOfReads; ++index)
        {
            readIds.add(coordinator.read(13, index));
        }

        for (int readId : readIds)
        {
            ReadRequestResult result = null;
            while (result == null || !result.isDone())
            {
                result = coordinator.readResult(readId);
            }

            assertTrue("Read should succeed", result.wasSuccessful());
        }
    }

    @Test(timeout=10000)
    public void testAddDataNode()
    {
        System.out.println("\nStatuses before adding a node:");
        reportStatuses();

        int id = 15;
        String host = "localhost";
        int port = DataNodeMain.DEFAULT_PORT + id;
        DataNodeIdentifier nodeId = new DataNodeIdentifier(id, host, port);

        List<String> driveHandles = new ArrayList<String>();
        driveHandles.add("data/drive." + id);
        List<Long> driveSizes = new ArrayList<Long>();
        driveSizes.add(1024L);
        
        DataNodeMain main = new DataNodeMain(port, blockSize, driveHandles, driveSizes);
        main.start();

        int requestId = coordinator.addDataNode(nodeId);

        while (!coordinator.requestFinished(requestId))
        {
            // spin!!!
        }

        System.out.println("\nStatuses after adding a node:");
        reportStatuses();

        float totalSegments = 0.0f;
        for (DataNodeStatusPair eachPair : coordinator.getDataNodeStatusPairs())
        {
            totalSegments += eachPair.getStatus().getSegmentsStored();
        }

        float averageSegments = totalSegments / coordinator.getDataNodeStatusPairs().size();
        float lowerBound = 0.99f * averageSegments - coordinator.getSegmentsPerSegmentGroup();
        float upperBound = 1.01f * averageSegments + coordinator.getSegmentsPerSegmentGroup();

        for (DataNodeStatusPair eachPair : coordinator.getDataNodeStatusPairs())
        {
            assertTrue("Should be within range", lowerBound <= eachPair.getStatus().getSegmentsStored());
            assertTrue("Should be within range", eachPair.getStatus().getSegmentsStored() <= upperBound);
        }
    }

    @Test(timeout=10000)
    public void testRemoveDataNode()
    {
        DataNodeIdentifier killedNode = nodes.get(0);
        DataNodeIdentifier secondKilledNode = nodes.get(1);
        System.out.println("Removing " + killedNode);

        int requestId = coordinator.removeDataNode(killedNode);

        while (!coordinator.requestFinished(requestId))
        {
            // spin!!!
        }

        System.out.println("\nStatuses after removing a node:");
        reportStatuses();

        int secondRequestId = coordinator.removeDataNode(secondKilledNode);

        while (!coordinator.requestFinished(secondRequestId))
        {
            // spin!!!
        }

        System.out.println("\nStatuses after removing a node:");
        reportStatuses();

        float totalSegments = 0.0f;
        for (DataNodeStatusPair eachPair : coordinator.getDataNodeStatusPairs())
        {
            totalSegments += eachPair.getStatus().getSegmentsStored();
        }

        float averageSegments = totalSegments / coordinator.getDataNodeStatusPairs().size();
        float lowerBound = 0.99f * averageSegments - coordinator.getSegmentsPerSegmentGroup();
        float upperBound = 1.01f * averageSegments + coordinator.getSegmentsPerSegmentGroup();

        for (DataNodeStatusPair eachPair : coordinator.getDataNodeStatusPairs())
        {
            assertTrue("Should be within range", lowerBound <= eachPair.getStatus().getSegmentsStored());
            assertTrue("Should be within range", eachPair.getStatus().getSegmentsStored() <= upperBound);
        }
    }

    public void reportStatuses()
    {
        List<DataNodeStatusPair> datanodeStatusPairs = coordinator.getDataNodeStatusPairs();

        for (DataNodeStatusPair eachPair : datanodeStatusPairs)
        {
            System.out.println(eachPair);
        }
    }
}


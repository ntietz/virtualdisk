package com.virtualdisk.coordinator;


import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;
import java.io.*;

public class CoordinatorTest
{
    private static Integer clusterSize = 10;
    private static Integer blockSize = 10;
    private static Integer segmentSize = 10;
    private static Integer segmentGroupSize = 5;
    private static Integer quorumSize = 3;
    private static List<DataNodeIdentifier> dataNodes;
    private static NetworkServer server;
    private static Coordinator coordinator;

    private static final byte[] emptyBlock = new byte[blockSize];
    private static final long seed = 2011;

    @BeforeClass
    public static void init()
    {
        for (int index = 0; index < blockSize; ++index)
        {
            emptyBlock[index] = 0;
        }
    }

    @Before
    public void setup()
    throws IOException
    {
        server = new FakeReliableNetworkServer();
        dataNodes = ((FakeReliableNetworkServer)server)
                        .generateDataNodes(clusterSize, blockSize, 1000, 3);

        coordinator = new Coordinator( blockSize
                                     , segmentSize
                                     , segmentGroupSize
                                     , quorumSize
                                     , dataNodes
                                     , server
                                     );

    }

    @Test
    public void testCreateVolume()
    {
        boolean firstTryCreate = coordinator.createVolume(0);
        boolean secondTryCreate = coordinator.createVolume(0);

        boolean firstTryDelete = coordinator.deleteVolume(0);
        boolean secondTryDelete = coordinator.deleteVolume(0);

        assertEquals("First volume creation should succeed.", true, firstTryCreate);
        assertEquals("Second volume creation should fail.", false, secondTryCreate);
        assertEquals("First volume deletion should succeed.", true, firstTryDelete);
        assertEquals("Second volume deletion should fail.", false, secondTryDelete);

    }

    @Test
    public void testGenerateNewRequestId()
    {
        int firstId = coordinator.generateNewRequestId();
        int secondId = coordinator.generateNewRequestId();
        int thirdId = coordinator.generateNewRequestId();

        assertFalse("ids should not match.", firstId == secondId);
        assertFalse("ids should not match.", secondId == thirdId);
    }

    @Test
    public void testGetNewTimestamp()
    {
        Date firstTs = coordinator.getNewTimestamp();
        Date secondTs = coordinator.getNewTimestamp();
        Date thirdTs = coordinator.getNewTimestamp();

        assertTrue("Timestamps should be correctly ordered.", firstTs.before(secondTs));
        assertTrue("Timestamps should be correctly ordered.", secondTs.before(thirdTs));
    }

    @Test
    public void testReadWrite()
    throws Throwable
    {
        Random random = new Random(seed);
        Random testRandom = new Random(seed);

        boolean created = coordinator.createVolume(0);

        assertEquals("Volume creation should succeed.", true, created);

        // write a few blocks
        for (int index = 0; index < 5; ++index)
        {
            byte[] data = new byte[blockSize];
            random.nextBytes(data);

            int id = coordinator.write(0, index, data);

            int maxTries = 50;
            int count = 0;
            while (count < maxTries && coordinator.writeResultMap.get(id) == null)
            {
                Thread.sleep(10);
                ++count;
            }

            assertTrue("Write id should be valid.", coordinator.writeResultMap.get(id) != null);
            assertTrue("Write should complete.", coordinator.writeCompleted(id));
            assertTrue("Write should succeed.", coordinator.writeResult(id));
        }

        // read the blocks to verify they are valid
        for (int index = 0; index < 5; ++index)
        {
            byte[] expected = new byte[blockSize];
            testRandom.nextBytes(expected);

            int id = coordinator.read(0, index);

            int maxTries = 50;
            int count = 0;
            while (count < maxTries && coordinator.readResultMap.get(id) == null)
            {
                Thread.sleep(10);
                ++count;
            }

            assertTrue("Read id should be valid.", coordinator.readResultMap.get(id) != null);
            assertTrue("Read should complete.", coordinator.readCompleted(id));
            assertArrayEquals("Read data should match.", expected, coordinator.readResult(id));
        }
    }

    @Test
    public void testSegmentGroupAssignment()
    {
        coordinator.createVolume(0);

        for (int index = 0; index < 5; ++index)
        {
            coordinator.write(0, index, emptyBlock);
        }

        SegmentGroup first = coordinator.getSegmentGroup(0, 0);
        SegmentGroup second = coordinator.getSegmentGroup(0, 1);
        SegmentGroup third = coordinator.getSegmentGroup(0, 2);

        Set<DataNodeIdentifier> firstIds = new HashSet<DataNodeIdentifier>(first.getMembers());
        Set<DataNodeIdentifier> secondIds = new HashSet<DataNodeIdentifier>(second.getMembers());
        Set<DataNodeIdentifier> thirdIds = new HashSet<DataNodeIdentifier>(third.getMembers());

        Set<DataNodeIdentifier> firstRemoveSecond = firstIds;
        firstRemoveSecond.removeAll(secondIds);

        assertTrue("First should not contain second.", firstIds.containsAll(firstRemoveSecond));
        assertTrue("First should be same size, removing second.", firstIds.size() == firstRemoveSecond.size());
    }
}


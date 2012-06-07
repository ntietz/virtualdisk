package com.virtualdisk.coordinator;

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

    @Test(timeout=10000)
    public void testReadWrite()
    throws Throwable
    {
        Random random = new Random(seed);
        Random testRandom = new Random(seed);

        boolean created = coordinator.createVolume(0);

        assertEquals("Volume creation should succeed.", true, created);
        
        int numberOfRounds = 100;

        List<Integer> writeIds = new ArrayList<Integer>();
        
        // write a few blocks
        for (int index = 0; index < numberOfRounds; ++index)
        {
            byte[] data = new byte[blockSize];
            random.nextBytes(data);
            
            writeIds.add(coordinator.write(0, index, data));
        }
        
        for (int index = 0; index < numberOfRounds; ++index)
        {
            int id = writeIds.get(index);
            
            while (!coordinator.writeCompleted(id))
            {
                // spin!!!
            }

            assertTrue("Write id should be valid." + index, coordinator.writeResultMap.get(id) != null);
            assertTrue("Write should complete.", coordinator.writeCompleted(id));
            assertTrue("Write should succeed.", coordinator.writeResult(id));
        }
        
        List<Integer> readIds = new ArrayList<Integer>();

        // read the blocks to verify they are valid
        for (int index = 0; index < numberOfRounds; ++index)
        {
            readIds.add(coordinator.read(0, index));
        }
        
        for (int index = 0; index < numberOfRounds; ++index)
        {
            int id = readIds.get(index);
            
            while (!coordinator.readCompleted(id))
            {
                // spin!!!
            }
            
            byte[] expected = new byte[blockSize];
            testRandom.nextBytes(expected);

            assertTrue("Read id should be valid. " + index, coordinator.readResultMap.get(id) != null);
            assertTrue("Read should complete.", coordinator.readCompleted(id));
            assertArrayEquals("Read data should match.", expected, coordinator.readResult(id));
        }
    }

    @Test
    public void testSegmentGroupAssignment()
    {
        coordinator.createVolume(0);
        
        int numberOfWrites = 5113;
        
        for (int index = 0; index < numberOfWrites; ++index)
        {
            coordinator.assignSegmentGroup(0, index);
        }
        
        List<Long> segmentsStored = new ArrayList<Long>();
                
        int size = coordinator.datanodeStatuses.size();
        for (int index = 0; index < size; ++index)
        {
            segmentsStored.add(coordinator.datanodeStatuses.poll().getStatus().getSegmentsStored());
        }
        
        long totalSegmentsStored = 0;
        
        for (long each : segmentsStored)
        {
            totalSegmentsStored += each;
        }
        
        float average = ((float)totalSegmentsStored)/segmentsStored.size();
        
        for (long each : segmentsStored)
        {
            float error = Math.abs((each - average)/average);
            assertTrue("Error should be low.", error < 0.05);
        }
    }
}


package com.virtualdisk.coordinator;

import com.virtualdisk.network.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;
import java.io.*;

public class CoordinatorTest
{
    private static int clusterSize = 10;
    private static int blockSize = 10;
    private static int segmentSize = 10;
    private static int segmentGroupSize = 5;
    private static int quorumSize = 3;
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
    public void testGetters()
    {
        assertEquals("Block size should match", (int)blockSize, coordinator.getBlockSize());
        assertEquals("Segment size should match", (int)segmentSize, coordinator.getSegmentSize());
        assertEquals("Segment group size should match", (int)segmentGroupSize, coordinator.getSegmentGroupSize());
    }

    @Test(timeout=10000)
    public void testCreateVolume()
    {
        int firstCreateId = coordinator.createVolume(0);
        int firstDeleteId = coordinator.deleteVolume(0);

        while (!coordinator.requestFinished(firstCreateId));
        while (!coordinator.requestFinished(firstDeleteId));

        boolean firstTryCreate = coordinator.createVolumeResult(firstCreateId).wasSuccessful();
        boolean firstTryDelete = coordinator.deleteVolumeResult(firstDeleteId).wasSuccessful();

        assertEquals("First volume creation should succeed.", true, firstTryCreate);
        assertEquals("First volume deletion should succeed.", true, firstTryDelete);
    }

    @Test
    public void testGetNewTimestamp()
    throws Exception
    {
        Date firstTs = coordinator.getTimestamp();
        Thread.sleep(10);
        Date secondTs = coordinator.getTimestamp();
        Thread.sleep(10);
        Date thirdTs = coordinator.getTimestamp();

        assertTrue("Timestamps should be correctly ordered.", firstTs.before(secondTs));
        assertTrue("Timestamps should be correctly ordered.", secondTs.before(thirdTs));
    }

    @Test(timeout=10000)
    public void testReadWrite()
    throws Throwable
    {
        Random random = new Random(seed);
        Random testRandom = new Random(seed);

        int createId = coordinator.createVolume(0);
        while (!coordinator.requestFinished(createId));
        boolean created = coordinator.createVolumeResult(createId).wasSuccessful();

        assertEquals("Volume creation should succeed.", true, created);

        int existsId = coordinator.volumeExists(0);
        while (!coordinator.requestFinished(existsId));
        boolean exists = coordinator.volumeExistsResult(existsId).volumeExists();

        assertEquals("Volume should exist", true, exists);

        existsId = coordinator.volumeExists(13);
        while (!coordinator.requestFinished(existsId));
        exists = coordinator.volumeExistsResult(existsId).volumeExists();

        assertEquals("Volume should not exist", false, exists);
        
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
            
            while (!coordinator.requestFinished(id))
            {
                // spin!!!
            }

            assertTrue("Write should complete.", coordinator.requestFinished(id));
            assertTrue("Write should succeed.", coordinator.writeResult(id).wasSuccessful());
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
            
            while (!coordinator.requestFinished(id))
            {
                // spin!!!
            }
            
            byte[] expected = new byte[blockSize];
            testRandom.nextBytes(expected);

            assertTrue("Read should complete.", coordinator.requestFinished(id));
            assertArrayEquals("Read data should match.", expected, ((ReadRequestResult)coordinator.readResult(id)).getBlock());
        }
    }
}


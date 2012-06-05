package com.virtualdisk.datanode;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataNodeTest
{
    private static final int blockSize = 8;
    private static final List<Long> driveSizes = new ArrayList<Long>();
    static {
        driveSizes.add(10L);
        driveSizes.add(20L);
        driveSizes.add(30L);
    }
    private static final List<String> handles = new ArrayList<String>();
    static {
        handles.add("data/fakedrive1");
        handles.add("data/fakedrive2");
        handles.add("data/fakedrive3");
    }
    private static final byte[] emptyBlock = new byte[blockSize];
    private static final long seed = 2012; // seed used for consistent random numbers

    @BeforeClass
    public static void setup() throws Throwable
    {
        // initialize the empty block
        for (int index = 0; index < blockSize; ++index)
        {
            emptyBlock[index] = 0;
        }

        // initialize the empty hard drives
        for (int index = 0; index < handles.size(); ++index)
        {
            File f = new File(handles.get(index));
            RandomAccessFile out = new RandomAccessFile(f, "rw");

            out.seek(0);

            for (int loc = 0; loc < driveSizes.get(index); ++loc)
            {
                out.write(emptyBlock);
            }
        }
    }
    
    @Test
    public void testOperations()
    {
        Random random = new Random(seed);
        Random confirmRandom = new Random(seed);
        
        DataNode dataNode = new DataNode(blockSize, handles, driveSizes);
        
        assertEquals("Free space should match.", 60L, dataNode.totalFreeSpace());
        
        dataNode.createVolume(0);

        assertNull("Unordered location should be null", dataNode.getOrderTimestamp(0, 0L));
        assertTrue("Order should succeed", dataNode.order(0, 0L, new Date(0)));
        assertFalse("Order should fail", dataNode.order(0, 0L, new Date(0)));
        assertTrue("Order should succeed", dataNode.order(0, 0L, new Date(1)));
    }

    @Test
    public void failTest()
    {
        fail("Not yet implemented");
    }

/*
        // write a few blocks, read them, verify them
        // check free space
        for (int index = 0; index < 5; ++index)
        {
            byte[] data = new byte[blockSize];
            random.nextBytes(data);

            assertFalse("Writing should fail without ordering.", datanode.write(0, index, data, new Date(1)));
            assertTrue("Ordering should not fail.", datanode.order(0, index, new Date(1)));
            assertTrue("Writing should not fail.", datanode.write(0, index, data, new Date(1)));
        }

        for (int index = 0; index < 5; ++index)
        {
            byte[] data = new byte[blockSize];
            testRandom.nextBytes(data);

            assertArrayEquals("Did not return correct data. Location: " + index, data, datanode.read(0, index));
        }

        // read unwritten blocks, verify null
        for (int index = 5; index < 10; ++index)
        {
            assertNull("Should return null.", datanode.read(0, index));
        }

        // create a second volume
        datanode.createVolume(1);
        //assertTrue("Volume creation should succeed.", datanode.createVolume(1));
        //assertFalse("Volume creation should fail.", datanode.createVolume(1));

        // write enough blocks to reach multiple HDDs, read them, verify them
        // check free space
        for (int index = 0; index < 40; ++index)
        {
            byte[] data = new byte[blockSize];
            random.nextBytes(data);

            assertFalse("Writing should fail without ordering.", datanode.write(1, index, data, new Date(1)));
            assertTrue("Ordering should not fail.", datanode.order(1, index, new Date(1)));
            assertTrue("Writing should not fail.", datanode.write(1, index, data, new Date(1)));
        }

        testRandom.setSeed(seed);
        for (int index = 0; index < 5; ++index)
        {
            byte[] data = new byte[blockSize];
            testRandom.nextBytes(data);

            assertArrayEquals("Did not return correct data.", data, datanode.read(0, index));
        }
        for (int index = 0; index < 40; ++index)
        {
            byte[] data = new byte[blockSize];
            testRandom.nextBytes(data);

            assertArrayEquals("Did not return correct data.", data, datanode.read(1, index));
        }
        long calculatedSpaceLeft = 10 + 20 + 30 - 40 - 5;
        assertEquals("Free space should match.", calculatedSpaceLeft, datanode.totalFreeSpace());

        // write enough blocks to fill all the HDDs, read them, verify them
        // check free space is 0
        for (int index = 40; index < 40 + calculatedSpaceLeft; ++index)
        {
            byte[] data = new byte[blockSize];
            random.nextBytes(data);

            assertTrue("Ordering should not fail.", datanode.order(1, index, new Date(2)));
            assertFalse("Writing should fail.", datanode.write(1, index, data, new Date(1)));
            assertTrue("Writing should not fail", datanode.write(1, index, data, new Date(2)));
        }
        calculatedSpaceLeft = 0;
        assertEquals("Drive should be full.", calculatedSpaceLeft, datanode.totalFreeSpace());

        // delete the first volume
        // check free space > 0
        assertTrue("Volume deletion should succeed.", datanode.deleteVolume(0));
        calculatedSpaceLeft += 5;
        assertEquals("Incorrect free space.", calculatedSpaceLeft, datanode.totalFreeSpace());

    }
*/
}


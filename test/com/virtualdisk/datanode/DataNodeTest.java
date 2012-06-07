package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.TestFile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
    private static final long initialFreeSpace;
    static {
        long f = 0;
        for (long each : driveSizes)
            f += each;
        initialFreeSpace = f;
    }

    @BeforeClass
    public static void setup() throws Throwable
    {
        // initialize the empty block
        for (int index = 0; index < blockSize; ++index)
        {
            emptyBlock[index] = 0;
        }
        
        for (int index = 0; index < handles.size(); ++index)
        {
            TestFile.createFile(new Drive(blockSize, driveSizes.get(index), handles.get(index)));
        }
    }
    
    @Test
    public void testOperations()
    {
        Random random = new Random(seed);
        Random confirmRandom = new Random(seed);
        
        DataNode dataNode = new DataNode(blockSize, handles, driveSizes);
        
        assertEquals("Free space should match.", initialFreeSpace, dataNode.totalFreeSpace());
        
        try
        {
            dataNode.order(0, 0L, new Date(0));
            fail("Ordering should have thrown exception");
        }
        catch (NullPointerException e)
        {
            // do nothing, expected behavior
        }
        
        dataNode.createVolume(0);

        assertNull("Unordered location should be null", dataNode.getOrderTimestamp(0, 0L));
        assertTrue("Order should succeed", dataNode.order(0, 0L, new Date(0)));
        assertFalse("Order should fail", dataNode.order(0, 0L, new Date(0)));
        
        for (int index = 0; index < 10; ++index)
        {
            assertTrue("Order should succeed.", dataNode.order(0, (long)index, new Date(1)));
        }
        
        for (int index = 0; index < 10; ++index)
        {
            byte[] block = new byte[blockSize];
            random.nextBytes(block);
            
            assertFalse("Write should fail.", dataNode.write(0, (long)index, block, new Date(0)));
            assertTrue("Write should succeed."+index, dataNode.write(0, (long)index, block, new Date(1)));
        }
        
        for (int index = 0; index < 10; ++index)
        {
            byte[] expectedBlock = new byte[blockSize];
            confirmRandom.nextBytes(expectedBlock);
            
            byte[] actualBlock = dataNode.read(0, (long)index);
            
            assertArrayEquals("Block values should match.", expectedBlock, actualBlock);
        }
        
        assertNull("Unwritten block should be null", dataNode.read(0, 10L));
        
        assertEquals("Free space should match.", initialFreeSpace-10, dataNode.totalFreeSpace());
        
        for (int index = 10; index < initialFreeSpace; ++index)
        {
            byte[] block = new byte[blockSize];
            random.nextBytes(block);
            
            assertTrue("Order should succeed", dataNode.order(0, (long)index, new Date(10)));
            assertTrue("Write should succeed", dataNode.write(0, (long)index, block, new Date(15)));
        }
        
        assertEquals("Node should be full", 0, dataNode.totalFreeSpace());
        
        try
        {
            dataNode.order(0, initialFreeSpace+1, new Date(20));
            dataNode.write(0, initialFreeSpace+1, emptyBlock, new Date(20));
            fail("Should throw exception");
        }
        catch (NullPointerException e)
        {
            // do nothing, expected behavior
        }
        
        assertTrue("Volume deletion should succeed", dataNode.deleteVolume(0));
        dataNode.createVolume(0);
        
        assertEquals("Drive should be empty", initialFreeSpace, dataNode.totalFreeSpace());
        
        for(int index = 0; index < initialFreeSpace; ++index)
        {
            assertNull("All blocks should be empty", dataNode.read(0, (long)index));
        }
    }

    @AfterClass
    public static void cleanUp()
    {
        TestFile.deleteFiles();
    }
}


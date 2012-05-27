package edu.kent.cs.virtualdisk.datanode;
import edu.kent.cs.virtualdisk.datanode.DataNode;
import java.util.Date;
import java.util.Random;
import java.io.File;
import java.io.RandomAccessFile;
import org.junit.*;
import static org.junit.Assert.*;

public class DataNodeTest
{

    private static final Integer blockSize = 8;
    private static final Integer[] driveSizes = {10, 20, 30};
    private static final String[] handles = {"data/fakedrive1", "data/fakedrive2", "data/fakedrive3"};

    private static final byte[] emptyBlock = new byte[blockSize];

    private static final long seed = 2011; // seed used for consistent random numbers

    @BeforeClass
    public static void setup() throws Throwable
    {
        // initialize the empty block
        for (int index = 0; index < blockSize; ++index)
        {
            emptyBlock[index] = 0;
        }

        // initialize the empty hard drives
        for (int index = 0; index < handles.length; ++index)
        {
            File f = new File(handles[index]);
            RandomAccessFile out = new RandomAccessFile(f, "rw");

            out.seek(0);

            for (int loc = 0; loc < driveSizes[index]; ++loc)
            {
                out.write(emptyBlock);
            }
        }
    }

    @Test
    public void testAll()
    {
        Random random = new Random(seed);
        Random testRandom = new Random(seed);

        DataNode datanode = new DataNode(blockSize, handles, driveSizes);

        // test initial conditions
        assertEquals("Write should fail.", false, datanode.write(0, 0, emptyBlock,new Date(1)));
        assertEquals("Read should be null.", null, datanode.read(0, 0));

        // create a new volume
        assertTrue("Creation of a volume should work.", datanode.createVolume(0));
        assertFalse("Should not create volume twice.", datanode.createVolume(0));

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
        assertTrue("Volume creation should succeed.", datanode.createVolume(1));
        assertFalse("Volume creation should fail.", datanode.createVolume(1));

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
        Integer calculatedSpaceLeft = 10 + 20 + 30 - 40 - 5;
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
}


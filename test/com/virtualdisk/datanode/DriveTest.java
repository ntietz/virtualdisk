package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.*;

import org.junit.*;

import java.io.*;
import java.util.*;

public class DriveTest
{
    public static final int seed = 2012;
    
    @Test
    public void testGetters()
    {
        int blockSize = 10;
        long driveSize = 20;
        String handle = "/foo/bar";
        
        Drive drive = new Drive(blockSize, driveSize, handle);
        
        assertEquals("Block size should match.", blockSize, drive.getBlockSize());
        assertEquals("Drive size should match.", driveSize, drive.getDriveSize());
        assertEquals("Handle should match.", handle, drive.getHandle());
    }

    @Test
    public void testReadWrite()
    throws FileNotFoundException, IOException
    {
        int blockSize = 10;
        long driveSize = 20;
        String handle = "fakedisk";
        
        Drive drive = new Drive(blockSize, driveSize, handle);
        
        TestFile.createFile(drive);
        
        Random random = new Random(seed);
        Random confirmRandom = new Random(seed);
        
        for (int index = 0; index < driveSize; ++index)
        {
            byte[] block = new byte[blockSize];
            random.nextBytes(block);
            
            drive.write(index, block);
        }
        
        for (int index = 0; index < driveSize; ++index)
        {
            byte[] expectedBlock = new byte[blockSize];
            confirmRandom.nextBytes(expectedBlock);
            
            byte[] readBlock = drive.read(index);
            
            assertArrayEquals("Block values should match.", expectedBlock, readBlock);
        }
        
        TestFile.deleteFile(drive);
        
        for (int index = 0; index < driveSize; ++index)
        {
            assertNull("Read should fail", drive.read(index));
        }
        
        File dir = new File(drive.getHandle());
        dir.mkdir();
        
        assertFalse("Write should fail", drive.write(0, new byte[drive.getBlockSize()]));
        
        dir.delete();
    }

    @AfterClass
    public static void cleanUp()
    {
        TestFile.deleteFiles();
    }
}

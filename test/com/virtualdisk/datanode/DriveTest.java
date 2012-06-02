package com.virtualdisk.datanode;

import static org.junit.Assert.*;

import com.virtualdisk.util.*;

import org.junit.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DriveTest
{
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
        
        fail("Not yet implemented");
    }

    @AfterClass
    public static void cleanUp()
    {
        TestFile.deleteFiles();
    }
}

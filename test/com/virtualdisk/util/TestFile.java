package com.virtualdisk.util;

import com.virtualdisk.datanode.*;

import java.io.*;
import java.util.*;

public class TestFile
{
    private static List<String> handles = Collections.synchronizedList(new ArrayList<String>());
    
    public static void createFile(Drive drive)
    throws FileNotFoundException, IOException
    {
        byte[] emptyBlock = TestFile.emptyBlock(drive.getBlockSize());
        
        // initialize the empty block
        for (int index = 0; index < drive.getBlockSize(); ++index)
        {
            emptyBlock[index] = 0;
        }
        
        handles.add(drive.getHandle());

        File f = new File(drive.getHandle());
        RandomAccessFile out = new RandomAccessFile(f, "rw");

        out.seek(0);

        for (int loc = 0; loc < drive.getDriveSize(); ++loc)
        {
            out.write(emptyBlock);
        }
    }
    
    public static byte[] emptyBlock(int blockSize)
    {
        byte[] emptyBlock = new byte[blockSize];
        
        // initialize the empty block
        for (int index = 0; index < blockSize; ++index)
        {
            emptyBlock[index] = 0;
        }
        
        return emptyBlock;
    }
    
    public static void deleteFile(Drive drive)
    {
        File f = new File(drive.getHandle());
        f.delete();
    }
    
    public static void deleteFiles()
    {
        for (String each : handles)
        {
            File f = new File(each);
            f.delete();
        }
    }
}

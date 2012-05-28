package com.virtualdisk.datanode;
import java.io.File;
import java.io.RandomAccessFile;

public class Drive
{
    protected int blockSize;
    protected int driveSize;
    protected String handle;

    public Drive(int bs, int ds, String h)
    {
        blockSize = bs;
        driveSize = ds;
        handle = h;
    }

    public int getBlockSize()
    {
        return blockSize;
    }

    public int getDriveSize()
    {
        return driveSize;
    }

    public String getHandle()
    {
        return handle;
    }

    public Boolean write(int physicalOffset, byte[] block)
    {
        try
        {
            File f = new File(handle);
            RandomAccessFile out = new RandomAccessFile(f, "rw");

            out.seek(physicalOffset * blockSize);

            out.write(block);

            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public byte[] read(int physicalOffset)
    {
        byte[] block = new byte[blockSize];

        try
        {
            File f = new File(handle);
            RandomAccessFile in = new RandomAccessFile(f, "r");

            in.seek(physicalOffset * blockSize);

            in.read(block);

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return block;
    }
}


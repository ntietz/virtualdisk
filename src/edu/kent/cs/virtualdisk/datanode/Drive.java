package edu.kent.cs.virtualdisk.datanode;
import edu.kent.cs.virtualdisk.util.Range;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.ArrayList;

public class Drive
{
    protected Integer blockSize;
    protected Integer driveSize;
    protected String handle;

    public Drive(Integer bs, Integer ds, String h)
    {
        blockSize = bs;
        driveSize = ds;
        handle = h;
    }

    public Integer getBlockSize()
    {
        return blockSize;
    }

    public Integer getDriveSize()
    {
        return driveSize;
    }

    public String getHandle()
    {
        return handle;
    }

    public Boolean write(Integer physicalOffset, byte[] block)
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

    public byte[] read(Integer physicalOffset)
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


package com.virtualdisk.datanode;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * Drive is a class which wraps operations on drives, such as writing, to handle all the drive seeking, reading, writing, etc.
 * 
 * @author  Nicholas Tietz
 */
public class Drive
{
    /**
     * The size of disk blocks in bytes. This size should match the block sizes in all the data nodes and the coordinators.
     */
    protected int blockSize;

    /**
     * The size of the disk in blocks. This is a long to allow effectively unlimited drive sizes; the actual limit for a drive size is over 2^64 bytse, which is sufficiently large for the foreseeable future.
     */
    protected long driveSize;

    /**
     * A string holding the handle for the drive. This should be something like "/dev/sda" to allow raw disk access on unix systems.
     */
    protected String handle;

    /**
     * A standard constructor.
     * @param   blockSize   the block size in bytes, which should match all the other nodes in the system
     * @param   driveSize   the drive size in blocks
     * @param   handle      the string handle for the drive, to allow raw disk access
     */
    public Drive(int blockSize, long driveSize, String handle)
    {
        this.blockSize = blockSize;
        this.driveSize = driveSize;
        this.handle = handle;
    }

    /**
     * Returns the block size the drive is configured for.
     * @return  the block size the drive is configured for
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * Returns the size of the drive, in blocks.
     * @return  the size of the drive in blocks
     */
    public long getDriveSize()
    {
        return driveSize;
    }

    /**
     * Returns the handle for the disk.
     * @return  the handle for the disk
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Writes a block to a physical location on the disk.
     * @param   physicalOffset  the phsyical offset we want to write the block
     * @param   block           the data we wish to write at that physical location
     * @return  true if the write succeeds, false if it fails due to an error
     */
    public boolean write(long physicalOffset, byte[] block)
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
            //e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Reads a block in from the disk at the supplied physical offset.
     * @param   physicalOffset  the physical offset we wish to read from
     * @return  the block at the given location, or null if there is any disk-read error
     */
    public byte[] read(long physicalOffset)
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
            //e.printStackTrace();
            return null;
        }

        return block;
    }
}


package com.virtualdisk.util;

/**
 * DriveOffsetPair is a convenience class, primarily for use in the DataNode class.
 * It is used for addressing physical locations are (drive, location) pairs.
 * 
 * @author  Nicholas Tietz
 */
public class DriveOffsetPair
{
    /**
     * The index within a list of drives to which this pair points.
     */
    protected int driveNumber;

    /**
     * The physical offset we are looking for.
     */
    protected long offset;

    /**
     * Standard constructor.
     * @param   driveNumber the drive we wish to point to, as an index within a list
     * @param   offset      the location we wish to point to on the disk
     */
    public DriveOffsetPair(int driveNumber, long offset)
    {
        this.driveNumber = driveNumber;
        this.offset = offset;
    }

    /**
     * @return  the drive number of the pair
     */
    public int getDriveNumber()
    {
        return driveNumber;
    }

    /**
     * @return  the offset of the pair
     */
    public long getOffset()
    {
        return offset;
    }

}

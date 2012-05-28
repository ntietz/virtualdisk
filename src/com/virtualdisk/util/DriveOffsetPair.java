package com.virtualdisk.util;

public class DriveOffsetPair
{
    protected int driveNumber;
    protected long offset;

    public DriveOffsetPair(int d, long o)
    {
        driveNumber = d;
        offset = o;
    }

    public int getDriveNumber()
    {
        return driveNumber;
    }

    public long getOffset()
    {
        return offset;
    }

}

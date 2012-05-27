package edu.kent.cs.virtualdisk.util;

public class DriveOffsetPair
{
    protected Integer driveNumber;
    protected Integer offset;

    public DriveOffsetPair(Integer d, Integer o)
    {
        driveNumber = d;
        offset = o;
    }

    public Integer getDriveNumber()
    {
        return driveNumber;
    }

    public Integer getOffset()
    {
        return offset;
    }

}


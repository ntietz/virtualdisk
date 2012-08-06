package com.virtualdisk.network.util;

public class DataNodeStatus
{
    private int blockSizeInBytes;
    private int segmentSizeInBlocks;
    //private int driveCapacityInSegments;
    private long segmentsStored;
    //private float percentFull;

    public DataNodeStatus(int blockSize, int segmentSize)
    {
        blockSizeInBytes = blockSize;
        segmentSizeInBlocks = segmentSize;

        segmentsStored = 0;
    }

    public long getSegmentsStored()
    {
        return segmentsStored;
    }

    public void addStoredSegments(int numberNewSegments)
    {
        segmentsStored += numberNewSegments;
    }

    public String toString()
    {
        return "status: { holding:" + segmentsStored + " }";
    }

}



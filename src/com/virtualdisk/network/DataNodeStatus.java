package com.virtualdisk.network;

public class DataNodeStatus
{

    protected int blockSizeInBytes;
    protected int segmentSizeInBlocks;
    protected int driveCapacityInSegments;

    protected long segmentsStored;
    protected float percentFull;

    public DataNodeStatus(int bs, int ss)
    {
        blockSizeInBytes = bs;
        segmentSizeInBlocks = ss;

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

}



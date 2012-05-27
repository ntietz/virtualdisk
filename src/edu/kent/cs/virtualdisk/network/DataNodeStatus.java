package edu.kent.cs.virtualdisk.network;

public class DataNodeStatus
{

    protected Integer blockSizeInBytes;
    protected Integer segmentSizeInBlocks;
    protected Integer driveCapacityInSegments;

    protected Integer segmentsStored;
    protected Float percentFull;

    public DataNodeStatus(Integer bs, Integer ss)
    {
        blockSizeInBytes = bs;
        segmentSizeInBlocks = ss;

        segmentsStored = 0;
    }

    public Integer getSegmentsStored()
    {
        return segmentsStored;
    }

    public void addStoredSegments(Integer numberNewSegments)
    {
        segmentsStored += numberNewSegments;
    }

}



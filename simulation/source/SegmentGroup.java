package simulation;

import java.util.*;

public class SegmentGroup
{
    private long start;
    private long stop; // stop == (start + segmentGroupSize - 1)
    private List<DataNodeId> nodes;

    public SegmentGroup(long start, long stop, List<DataNodeId> nodes)
    {
        this.start = start;
        this.stop = stop;
        this.nodes = nodes;
    }

    public boolean contains(long offset)
    {
        return (start <= offset && offset <= stop);
    }

    public List<DataNodeId> getNodes()
    {
        return nodes;
    }
}


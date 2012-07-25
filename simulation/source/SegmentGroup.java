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

    public boolean contains(DataNodeId node)
    {
        return nodes.contains(node);
    }

    public void replace(DataNodeId original, DataNodeId replacement)
    {
        int index = nodes.indexOf(original);
        nodes.set(index, replacement);
        // FIXME in real life, we would retain both nodes until the values are all copied over
    }

    public List<DataNodeId> getNodes()
    {
        return nodes;
    }
}


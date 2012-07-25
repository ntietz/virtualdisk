package simulation;

import java.util.*;

public class VolumeTable
{
    // maps the starting segment number to the containing segment group
    private Map<Long, SegmentGroup> resolutionMap;

    private int nodesPerSegmentGroup;
    private int blocksPerSegment;
    private int segmentsPerSegmentGroup;

    public VolumeTable(int nodesPerSegmentGroup, int blocksPerSegment, int segmentsPerSegmentGroup)
    {
        resolutionMap = new HashMap();
        this.nodesPerSegmentGroup = nodesPerSegmentGroup;
        this.blocksPerSegment = blocksPerSegment;
        this. segmentsPerSegmentGroup = segmentsPerSegmentGroup;
    }

    public SegmentGroup getSegmentGroup(long logicalOffset)
    {
        return resolutionMap.get(getStartingSegment(logicalOffset));
    }

    public void setSegmentGroup(long logicalOffset, SegmentGroup segmentGroup)
    {
        resolutionMap.put(getStartingSegment(logicalOffset), segmentGroup);

        for (DataNodeId each : segmentGroup.getNodes())
        {
            each.updateSegmentGroupMemberships(1);
        }
    }

    public List<SegmentGroup> getAllSegmentGroups()
    {
        return new ArrayList(resolutionMap.values());
    }

    public long getStartingSegment(long logicalOffset)
    {
        long segmentNumber = (logicalOffset / blocksPerSegment);
        long startingSegment = segmentNumber - (segmentNumber % segmentsPerSegmentGroup);
        return startingSegment;
    }
}


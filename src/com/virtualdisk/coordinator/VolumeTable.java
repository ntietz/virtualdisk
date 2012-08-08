package com.virtualdisk.coordinator;

import com.virtualdisk.network.util.*;

import java.util.*;
import java.util.concurrent.*;

public class VolumeTable
{
    private Map<Integer, Map<Long, SegmentGroup>> table;
    private int bytesPerBlock;
    private int blocksPerSegment;
    private int segmentsPerSegmentGroup;
    private int nodesPerSegmentGroup;

    public VolumeTable( int bytesPerBlock
                      , int blocksPerSegment
                      , int segmentsPerSegmentGroup
                      , int nodesPerSegmentGroup
                      )
    {
        table = new ConcurrentHashMap<Integer, Map<Long, SegmentGroup>>();
        this.bytesPerBlock = bytesPerBlock;
        this.blocksPerSegment = blocksPerSegment;
        this.segmentsPerSegmentGroup = segmentsPerSegmentGroup;
        this.nodesPerSegmentGroup = nodesPerSegmentGroup;
    }

    public void addVolume(int volumeId)
    {
        Map<Long, SegmentGroup> volumeMap = new ConcurrentHashMap<Long, SegmentGroup>();
        table.put(volumeId, volumeMap);
    }

    public void deleteVolume(int volumeId)
    {
        table.remove(volumeId);
    }

    public SegmentGroup getSegmentGroup(int volumeId, long logicalOffset)
    {
        // TODO consider adding a null-check here
        long startingSegment = startOf(logicalOffset);
        return table.get(volumeId).get(startingSegment);
    }

    public void setSegmentGroup(int volumeId, long logicalOffset, SegmentGroup segmentGroup)
    {
        long startingSegment = startOf(logicalOffset);
        table.get(volumeId).put(startingSegment, segmentGroup);
    }

    public void unsetSegmentGroup(int volumeId, long logicalOffset)
    {
        long startingSegment = startOf(logicalOffset);
        table.get(volumeId).remove(startingSegment);
    }

    public List<SegmentGroup> getAllSegmentGroups()
    {
        List<SegmentGroup> segmentGroups = new ArrayList<SegmentGroup>();

        for (Integer eachKey : table.keySet())
        {
            Map<Long, SegmentGroup> segmentGroupMap = table.get(eachKey);
            for (SegmentGroup eachGroup : segmentGroupMap.values())
            {
                segmentGroups.add(eachGroup);
            }
        }

        return segmentGroups;
    }

    public List<SegmentGroup> getAllSegmentGroupsContaining(DataNodeIdentifier node)
    {
        List<SegmentGroup> segmentGroups = new ArrayList<SegmentGroup>();

        for (Integer eachKey : table.keySet())
        {
            Map<Long, SegmentGroup> segmentGroupMap = table.get(eachKey);
            for (SegmentGroup eachGroup : segmentGroupMap.values())
            {
                if (eachGroup.isMember(node))
                {
                    segmentGroups.add(eachGroup);
                }
            }
        }

        return segmentGroups;
    }

    public long startOf(long logicalOffset)
    {
        long startingBlock = logicalOffset - (logicalOffset % (blocksPerSegment*segmentsPerSegmentGroup));
        return startingBlock;
    }

    // the last included block
    public long endOf(long logicalOffset)
    {
        return startOf(logicalOffset) + blocksPerSegment*segmentsPerSegmentGroup - 1;
    }

    public SegmentGroup makeSegmentGroup(List<DataNodeIdentifier> members, int volumeId, long logicalOffset)
    {
        return new SegmentGroup(members, volumeId, startOf(logicalOffset), endOf(logicalOffset));
    }

}


package simulation;

import java.util.*;

// emulates some functionality of the coordinator and serves as the entry point to the test
public class Coordinator
{
    public static void main(String... args)
    {
        int numberOfNodes = 8;

        List<DataNodeId> nodes = new ArrayList();
        for (int index = 0; index < numberOfNodes; ++index)
        {
            nodes.add(new DataNodeId(index));
        }

        Coordinator coordinator = new Coordinator(nodes, 3, 10, 20);

        for (long index = 0; index < numberOfNodes * 5010; ++index)
        {
            coordinator.write(index);
        }

        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }
    }

    private List<DataNodeId> nodes;
    private VolumeTable volumeTable;

    private int nodesPerSegmentGroup;
    private int blocksPerSegment;
    private int segmentsPerSegmentGroup;

    public Coordinator( List<DataNodeId> nodes
                      , int nodesPerSegmentGroup
                      , int blocksPerSegment
                      , int segmentsPerSegmentGroup
                      )
    {
        this.nodes = nodes;
        volumeTable = new VolumeTable(nodesPerSegmentGroup, blocksPerSegment, segmentsPerSegmentGroup);
        this.nodesPerSegmentGroup = nodesPerSegmentGroup;
        this.blocksPerSegment = blocksPerSegment;
        this.segmentsPerSegmentGroup = segmentsPerSegmentGroup;
    }

    public List<DataNodeId> getNodes()
    {
        return nodes;
    }

    public void write(long logicalOffset)
    {
        SegmentGroup targetGroup = volumeTable.getSegmentGroup(logicalOffset);

        if (targetGroup == null)
        {
            targetGroup = generateSegmentGroup(volumeTable.getStartingSegment(logicalOffset));
            volumeTable.setSegmentGroup(logicalOffset, targetGroup);
        }

        // TODO simulate a write?
    }

    public SegmentGroup generateSegmentGroup(long startingSegment)
    {
        PriorityQueue<DataNodeId> nodeHeap = new PriorityQueue();

        for (DataNodeId node : nodes)
        {
            nodeHeap.add(node);
        }

        List<DataNodeId> members = new ArrayList();

        for (int index = 0; index < nodesPerSegmentGroup; ++index)
        {
            members.add(nodeHeap.poll());
        }

        long stoppingSegment = startingSegment + segmentsPerSegmentGroup - 1;
        return new SegmentGroup(startingSegment, stoppingSegment, members);
    }
}


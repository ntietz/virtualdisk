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

        for (long index = 0; index < numberOfNodes * 5000; ++index)
        {
            coordinator.write(index);
        }

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        coordinator.removeDataNode(nodes.get(2));

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        coordinator.removeDataNode(nodes.get(6));

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        coordinator.removeDataNode(nodes.get(5));

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        DataNodeId revivedNode = new DataNodeId(numberOfNodes + 1);
        nodes.add(revivedNode);
        coordinator.addDataNode(revivedNode);

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        revivedNode = new DataNodeId(numberOfNodes + 10);
        nodes.add(revivedNode);
        coordinator.addDataNode(revivedNode);

        System.out.println();
        for (DataNodeId each : nodes)
        {
            System.out.println(each.getNumericId() + ": " + each.getSegmentGroupMemberships() + " segment groups");
        }

        revivedNode = new DataNodeId(numberOfNodes + 13);
        nodes.add(revivedNode);
        coordinator.addDataNode(revivedNode);

        System.out.println();
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
        this.nodes = new ArrayList(nodes);
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
        // TODO perhaps increment a counter
    }

    public void read(long logicalOffset)
    {
        // TODO simulate a read?
        // TODO perhaps increment a counter
    }

    public void removeDataNode(DataNodeId original)
    {
        List<SegmentGroup> affectedSegmentGroups = new ArrayList();

        // FIXME In real code, each node's status would store what segment groups it belongs to
        // in order to reduce the burden of rebalancing
        System.out.println("Number of segment groups in system: " + volumeTable.getAllSegmentGroups().size());
        for (SegmentGroup each : volumeTable.getAllSegmentGroups())
        {
            if (each.contains(original))
            {
                affectedSegmentGroups.add(each);
            }
        }

        System.out.println("Affected segment groups: " + affectedSegmentGroups.size());

        for (SegmentGroup each : affectedSegmentGroups)
        {
            // FIXME perform a copy of the value from the old nodes to the new one
            PriorityQueue<DataNodeId> nodeHeap = new PriorityQueue();
            for (DataNodeId candidate : nodes)
            {
                if (!candidate.equals(original) && !each.contains(candidate))
                {
                    nodeHeap.add(candidate);
                }
            }

            DataNodeId lightest = nodeHeap.poll();
            each.replace(original, lightest);
            nodeHeap.add(lightest);
        }

        nodes.remove(original);
    }

    public void addDataNode(DataNodeId newcomer)
    {
        int numberOfNodes = nodes.size() + 1;
        int totalSegments = 0;

        for (DataNodeId each : nodes)
        {
            totalSegments += each.getSegmentGroupMemberships();
        }

        int averageSegments = totalSegments / numberOfNodes;

        while (!closeTo(newcomer.getSegmentGroupMemberships(), averageSegments))
        {
            PriorityQueue<DataNodeId> nodeHeap = new PriorityQueue(nodes.size(), new Comparator() {
                public int compare(Object leftObj, Object rightObj) {
                    DataNodeId left = (DataNodeId) leftObj;
                    DataNodeId right = (DataNodeId) rightObj;
                    return -1 * left.compareTo(right);
                }
            });

            for (DataNodeId each : nodes)
            {
                nodeHeap.add(each);
            }

            DataNodeId loser = nodeHeap.poll();
            SegmentGroup group = null;

            for (SegmentGroup each : volumeTable.getAllSegmentGroups())
            {
                if (each.contains(loser) && !each.contains(newcomer))
                {
                    group = each;
                    break;
                }
            }

            group.replace(loser, newcomer);
        }

        nodes.add(newcomer);
    }

    private boolean closeTo(int value, int bound)
    {
        return (bound <= value) && (value <= bound+1);
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


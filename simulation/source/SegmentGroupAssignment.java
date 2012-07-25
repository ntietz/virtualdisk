import java.util.*;

public class SegmentGroupAssignment
{
    public static void main(String... args)
    {
        SegmentGroupAssignment simulation = new SegmentGroupAssignment();
        simulation.run();
    }

    public void run()
    {
        for (int segmentGroupSize = 3; segmentGroupSize <= 5; ++segmentGroupSize)
        {
            System.out.println("-------------------------------------------");
            System.out.println("Segment group size: " + segmentGroupSize);
            System.out.println();

            for (int numberOfNodes = 8; numberOfNodes <= 20; ++numberOfNodes)
            {
                // all the nodes
                List<Node> nodes = new ArrayList();

                for (int index = 0; index < numberOfNodes; ++index)
                {
                    nodes.add(new Node(index));
                }

                System.out.println("Generated " + numberOfNodes + " nodes.");
                List<SegmentGroup> segmentGroups = generateInitialSegmentGroups(segmentGroupSize, nodes);
                System.out.println("Assigned " + segmentGroups.size() + " segment groups.");
                printSegmentGroupAverageCount(nodes);
                System.out.println();
            }
        }
    }

    public List<SegmentGroup> generateInitialSegmentGroups(int segmentGroupSize, List<Node> nodes)
    {
        List<SegmentGroup> segmentGroups = new ArrayList();

        int collisions = 0;
        while (!allNodesFull(segmentGroups, nodes, 4))
        {
            PriorityQueue<Node> loadQueue = new PriorityQueue();
            for (Node node : nodes)
            {
                loadQueue.add(node);
            }

            List<Node> groupNodes = new ArrayList();
            for (int index = 0; index < segmentGroupSize; ++index)
            {
                groupNodes.add(loadQueue.poll());
            }

            SegmentGroup candidate = new SegmentGroup(groupNodes);
            while (segmentGroups.contains(candidate))
            {
                ++collisions;
                groupNodes.remove(groupNodes.size() - 1);
                groupNodes.add(0, loadQueue.poll());
                candidate = new SegmentGroup(groupNodes);
            }

            segmentGroups.add(candidate);
            for (Node each : groupNodes)
            {
                each.numberOfGroups += 1;
            }
        }

        System.out.println("Collisions: " + collisions);

        return segmentGroups;
    }

    // checks if all nodes have at least N segment groups
    public boolean allNodesFull(List<SegmentGroup> segmentGroups, List<Node> nodes, int threshold)
    {
        // nodeId -> number of segment groups
        Map<Integer, Integer> nodeGroupCount = new HashMap();

        for (Node node : nodes)
        {
            nodeGroupCount.put(node.id, 0);
            if (node.numberOfGroups < threshold)
                return false;
        }

        return true;
    }

    public void printSegmentGroupCounts(List<Node> nodes)
    {
        System.out.print("Nodes:");
        for (Node node : nodes)
        {
            System.out.print(" (" + node.id + "->" + node.numberOfGroups + ")");
        }
        System.out.println();
    }

    public void printSegmentGroupAverageCount(List<Node> nodes)
    {
        double numberOfGroups = 0.0;
        for (Node node : nodes)
        {
            numberOfGroups += node.numberOfGroups;
        }
        double average = numberOfGroups / nodes.size();
        System.out.println("Nodes have " + average + " groups (avg)");
    }



    public class SegmentGroup
    implements Comparable
    {
        public List<Integer> nodes = new ArrayList();
        public List<Integer> segments = new ArrayList();

        public SegmentGroup(List<Node> nodes)
        {
            for (Node node : nodes)
            {
                this.nodes.add(node.id);
            }
        }

        public boolean equals(Object obj)
        {
            SegmentGroup other = (SegmentGroup) obj;

            return nodes.containsAll(other.nodes)
                && other.nodes.containsAll(nodes);
        }

        public int compareTo(Object obj)
        {
            SegmentGroup other = (SegmentGroup) obj;
            return (segments.size() - other.segments.size());
        }
    }

    public class Node
    implements Comparable
    {
        public int id;
        public List<Integer> segments = new ArrayList();
        public int numberOfGroups = 0;

        public Node(int id)
        {
            this.id = id;
        }

        public int compareTo(Object obj)
        {
            Node other = (Node) obj;
            return (numberOfGroups - other.numberOfGroups);
        }
    }
}


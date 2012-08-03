package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;
import com.virtualdisk.network.util.*;

import java.util.*;
import java.util.concurrent.*;

public class DataNodeReconfigurationHandler
extends Handler
{
    private VolumeTable volumeTable;
    private List<DataNodeIdentifier> datanodes;
    private PriorityBlockingQueue<DataNodeStatusPair> datanodeStatuses;
    DataNodeIdentifier affectedNode;
    private boolean affectedNodeIsUp;

    public DataNodeReconfigurationHandler( VolumeTable volumeTable
                                         , List<DataNodeIdentifier> datanodes
                                         , PriorityBlockingQueue<DataNodeStatusPair> datanodeStatuses
                                         , DataNodeIdentifier affectedNode // the one to remove or add...
                                         , boolean affectedNodeIsUp
                                         , Coordinator coordinator
                                         )
    {
        this.volumeTable = volumeTable;
        this.datanodes = datanodes;
        this.datanodeStatuses = datanodeStatuses;
        this.affectedNode = affectedNode;
        this.affectedNodeIsUp = affectedNodeIsUp;
        this.coordinator = coordinator;
    }

    public void action()
    {
        /* what do we do? this is what we do...

            while not balanced:
                pick a segment group of the to put the new node in / remove the old node from
                run the reconfigureSegmentGroup() function

            when all reconfiguration is done:
                if (oldNodeIsUp):
                    coordinator.attachDatanode(affectedNode)
                else
                    coordinator.detachDatanode(affectedNode)
                
                signal that all reconfiguration is done (set the result and let the handler terminate)
        */

        while(!coordinator.startReconfiguration())
        {
            // spin!!!
        }

        if (!affectedNodeIsUp)
        {
            List<SegmentGroup> affectedGroups = volumeTable.getAllSegmentGroupsContaining(affectedNode);

            for (SegmentGroup eachGroup : affectedGroups)
            {
                DataNodeIdentifier replacementNode = pickReplacement(eachGroup, affectedNode);
                reconfigureSegmentGroup(eachGroup, affectedNode, replacementNode, false);
                updateStatus(affectedNode, replacementNode);
            }
        }

        if (affectedNodeIsUp)
        {
            coordinator.attachDataNode(affectedNode);
        }
        else
        {
            coordinator.detachDataNode(affectedNode);
        }

        /*
            "while not balanced"
            balanced means that all nodes have close to the same number of segment groups assigned
            1. determine appropriate average load range (+- k segments)...
                this could be something like [0.95 * average - 1, 1.05 * average + 1] (the +/- 1 is for 
                extremely low-load clusters, where not everything might have an assignment; like when you first
                turn on the cluster)
            2. if any nodes are below the range, assign them to a segment group
               else if any nodes are above the range, remove them from a group
               rinse-and-repeat until the cluster is balanced appropriately
        */

        float numberOfSegmentGroupsAssigned = 0.0f;
        int numberOfNodes = datanodes.size();
        for (DataNodeStatusPair eachPair : datanodeStatuses)
        {
            DataNodeStatus status = eachPair.getStatus();
            numberOfSegmentGroupsAssigned += status.getSegmentsStored();
        }

        float expectedLoad = numberOfSegmentGroupsAssigned / numberOfNodes;
        float lowerBound = (0.95f * expectedLoad) - 1;
        float upperBound = (1.05f * expectedLoad) + 1;

        List<DataNodeIdentifier> nodesBelow = new ArrayList<DataNodeIdentifier>();
        List<DataNodeIdentifier> nodesAbove = new ArrayList<DataNodeIdentifier>();

        for (DataNodeStatusPair eachPair : datanodeStatuses)
        {
            DataNodeStatus status = eachPair.getStatus();
            if (status.getSegmentsStored() < lowerBound)
            {
                nodesBelow.add(eachPair.getIdentifier());
            }
            else if (status.getSegmentsStored() > upperBound)
            {
                nodesAbove.add(eachPair.getIdentifier());
            }
        }

        boolean balanced = true;
        if (nodesBelow.size() > 0 || nodesAbove.size() > 0)
        {
            balanced = false;
        }

        while (!balanced)
        {
            nodesBelow.clear();
            nodesAbove.clear();
            for (DataNodeStatusPair eachPair : datanodeStatuses)
            {
                DataNodeStatus status = eachPair.getStatus();
                if (status.getSegmentsStored() < lowerBound)
                {
                    nodesBelow.add(eachPair.getIdentifier());
                }
                else if (status.getSegmentsStored() > upperBound)
                {
                    nodesAbove.add(eachPair.getIdentifier());
                }
            }

            if (nodesBelow.size() > 0)
            {
                DataNodeIdentifier replacementNode = nodesBelow.get(0);
                DataNodeIdentifier oldNode = pickHeaviestLoad(replacementNode);
                SegmentGroup affectedGroup = volumeTable.getAllSegmentGroupsContaining(oldNode).get(0);
                reconfigureSegmentGroup(affectedGroup, oldNode, replacementNode, true);
                updateStatus(oldNode, replacementNode);
            }
            else if (nodesAbove.size() > 0)
            {
                DataNodeIdentifier oldNode = nodesAbove.get(0);
                SegmentGroup affectedGroup = volumeTable.getAllSegmentGroupsContaining(oldNode).get(0);
                DataNodeIdentifier replacementNode = pickReplacement(affectedGroup, oldNode);
                reconfigureSegmentGroup(affectedGroup, oldNode, replacementNode, true);
            }
            else
            {
                balanced = true;
            }
        }

        coordinator.finishReconfiguration();

        requestResult = new ReconfigurationRequestResult(requestId, true, true);
        coordinator.setRequestResult(requestId, (RequestResult) requestResult);
    }

    private void updateStatus(DataNodeIdentifier oldNode, DataNodeIdentifier replacementNode)
    {
        int segmentsPerSegmentGroup = coordinator.getSegmentsPerSegmentGroup();

        DataNodeStatusPair oldPair = null;
        DataNodeStatusPair newPair = null;

        for (DataNodeStatusPair eachPair : datanodeStatuses)
        {
            if (oldNode.equals(eachPair.getIdentifier()))
            {
                oldPair = eachPair;
            }
            else if (replacementNode.equals(eachPair.getIdentifier()))
            {
                newPair = eachPair;
            }
        }

        oldPair.getStatus().addStoredSegments(-1*segmentsPerSegmentGroup);
        newPair.getStatus().addStoredSegments(segmentsPerSegmentGroup);

        datanodeStatuses.add(oldPair);
        datanodeStatuses.add(newPair);
    }

    private DataNodeIdentifier pickReplacement(SegmentGroup affectedGroup, DataNodeIdentifier oldNode)
    {
        DataNodeIdentifier replacementNode = null;
        List<DataNodeStatusPair> removedPairs = new ArrayList();

        while (replacementNode == null)
        {
            DataNodeStatusPair next = datanodeStatuses.poll();
            removedPairs.add(next);
            DataNodeIdentifier nextId = next.getIdentifier();
            if (!nextId.equals(oldNode) && !affectedGroup.isMember(nextId))
            {
                replacementNode = nextId;
            }
        }

        for (DataNodeStatusPair each : removedPairs)
        {
            datanodeStatuses.add(each);
        }

        return replacementNode;
    }

    private DataNodeIdentifier pickHeaviestLoad(DataNodeIdentifier otherNode)
    {
        DataNodeIdentifier replacementNode = null;
        List<DataNodeStatusPair> removedPairs = new ArrayList();

        while (replacementNode == null)
        {
            DataNodeStatusPair next = datanodeStatuses.poll();
            removedPairs.add(next);
            DataNodeIdentifier nextId = next.getIdentifier();
            if (!nextId.equals(otherNode))
            {
                replacementNode = nextId;
            }
        }

        for (DataNodeStatusPair each : removedPairs)
        {
            datanodeStatuses.add(each);
        }

        return replacementNode;
    }

    protected void reconfigureSegmentGroup( SegmentGroup affectedGroup
                                          , DataNodeIdentifier oldNode
                                          , DataNodeIdentifier replacementNode
                                          , boolean oldNodeIsUp
                                          )
    {
         /* what do we do? this is what we do...
            note: we do NOT worry about the status of the node here; we assume that is set by the coordinator

            for each segment in the segment group:
                if (oldNodeIsUp):
                    send an unset-segment request
                perform a read on the segment group
                send a write request to the new node

            swap the new node into the segment group
        */

       int volumeId = affectedGroup.getVolumeId();
        long startingOffset = affectedGroup.getStartingBlock();
        long stoppingOffset = affectedGroup.getStoppingBlock();

        if (oldNodeIsUp)
        {
            List<DataNodeIdentifier> targets = new ArrayList<DataNodeIdentifier>();
            targets.add(oldNode);
            int unsetId = coordinator.getServer().issueUnsetSegmentRequest(targets, volumeId, startingOffset, stoppingOffset);

            boolean waiting = true;

            while (waiting)
            {
                List<UnsetSegmentRequestResult> results = coordinator.getServer().getUnsetSegmentRequestResults(unsetId);
                if (results.get(0).wasSuccessful())
                {
                    waiting = false;
                }
            }
        }

        List<DataNodeIdentifier> replacementMembers = new ArrayList<DataNodeIdentifier>(affectedGroup.getMembers());
        int oldNodeIndex = replacementMembers.indexOf(oldNode);
        replacementMembers.set(oldNodeIndex, replacementNode);
        SegmentGroup replacementGroup = new SegmentGroup(replacementMembers, volumeId, startingOffset, stoppingOffset);

        for (long offset = startingOffset; offset <= stoppingOffset; ++offset)
        // TODO FIXME eventual improvement: make this whole loop asynchronous
        // that is, issue each read request, then as the results come in, issue the write requests
        {
            int readId = coordinator.read(volumeId, logicalOffset);
            while (!coordinator.requestFinished(readId))
            {
                // spin!!!
            }

            ReadRequestResult readResult = coordinator.readResult(readId);
            byte[] block = readResult.getBlock();

            int writeId = coordinator.writeWithTarget(replacementGroup, volumeId, offset, block);
            while (!coordinator.requestFinished(writeId))
            {
                // spin!!!
            }

            // TODO FIXME check for success of the read (above) and the write (here)
        }

        affectedGroup.replace(oldNode, replacementNode);
    }
}


package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

import java.util.*;

public class DataNodeReconfigurationHandler
extends Handler
{
    private SegmentGroup affectedGroup;
    private DataNodeIdentifier oldNode;
    private DataNodeIdentifier replacementNode;
    private boolean oldNodeIsUp;

    public DataNodeReconfigurationHandler( int volumeId
                                         , SegmentGroup affectedGroup
                                         , DataNodeIdentifier oldNode
                                         , DataNodeIdentifier replacementNode
                                         , boolean oldNodeIsUp
                                         )
    {
        this.volumeId = volumeId;
        this.logicalOffset = 0;
        this.affectedGroup = affectedGroup;
        this.oldNode = oldNode;
        this.replacementNode = replacementNode;
        this.oldNodeIsUp = oldNodeIsUp;
    }

    public void action()
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
        long startingOffset = affectedGroup.getStartingBlock();
        long stoppingOffset = affectedGroup.getStoppingBlock();

        if (oldNodeIsUp)
        {
            // TODO perform an unset request
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
        SegmentGroup replacementGroup = new SegmentGroup(replacementMembers, startingOffset, stoppingOffset);

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
        // TODO FIXME set the result here...
    }
}


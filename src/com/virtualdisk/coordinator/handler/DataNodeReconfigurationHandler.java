package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.util.*;

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
        this.logicalOffset = logicalOffset;
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

    }
}


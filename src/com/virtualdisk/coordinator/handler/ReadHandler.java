package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/**
 * The ReadHandler creates, issues, and manages read requests and their results.
 */
public class ReadHandler
extends Handler
{
    private SegmentGroup targets;

    /**
     * Standard constructor.
     * @param   volumeId        the volume we are writing to
     * @param   logicalOffset   the logical location of the write
     * @param   targets         the nodes we are writing to
     * @param   coordinator     the coordinator for the request
     */
    public ReadHandler( int volumeId
                      , long logicalOffset
                      , SegmentGroup targets
                      , Coordinator coordinator
                      )
    {
        this.volumeId = volumeId;
        this.logicalOffset = logicalOffset;
        this.coordinator = coordinator;
        this.targets = targets;
    }

    /**
     * This action issues the ReadRequest and waits to get a response.
     */
    @Override
    public void action()
    {
        int orderId = coordinator.getServer().issueReadRequest(targets, volumeId, logicalOffset);

        boolean waiting = true;
        boolean success = false;
        boolean timestampsMatch = true;
        byte[] value = null;
        Date timestamp = null;

        while (waiting)
        {
            List<ReadRequestResult> results = coordinator.getServer().getReadRequestResults(orderId);
            int completed = 0;

            for (ReadRequestResult each : results)
            {
                if (each.isDone())
                {
                    ++completed;

                    if (timestamp == null)
                    {
                        timestamp = each.getTimestamp();
                        value = each.getBlock();
                    }

                    if ( !each.getTimestamp().equals(timestamp) )
                    {
                        timestampsMatch = false;

                        if (each.getTimestamp().after(timestamp))
                        {
                            timestamp = each.getTimestamp();
                            value = each.getBlock();
                        }
                    }
                }
            }

            if (completed >= coordinator.getQuorumSize())
            {
                waiting = false;

                if (timestampsMatch)
                {
                    success = true;
                }
                else
                {
                    int id = coordinator.write(volumeId, logicalOffset, value);
                    while (!coordinator.requestFinished(id))
                    {
                        // spin!!!
                    }
                    
                    WriteRequestResult writeResult = coordinator.writeResult(id);
                    boolean writeSuccess = writeResult.wasSuccessful();
                    timestampsMatch = writeSuccess;
                    success = writeSuccess;
                }
            }
        }

        if (value == null)
        {
            value = new byte[0];
        }

        requestResult = new ReadRequestResult(requestId, true, success, timestamp, value);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
    }
}


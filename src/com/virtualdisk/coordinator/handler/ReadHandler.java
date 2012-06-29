package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/*
 * This class handles read requests.
 */
public class ReadHandler
extends Handler
{
    private byte[] result = null;
    private SegmentGroup targets;

    /*
     * Constructor takes in volume ID and logical offset to configure the read request.
     */
    public ReadHandler(int vid, long lo, SegmentGroup targets, Coordinator c)
    {
        volumeId = vid;
        logicalOffset = lo;
        coordinator = c;
        this.targets = targets;
    }

    /*
     * This method returns the result of the read request.
     */
    public byte[] getResult()
    {
        return result;
    }

    /*
     * This method performs a read request, as configured in the constructor.
     * At any point where the request may block on IO, blocking will cause the handler
     * to pause and execution will go to the next request handler in the queue.
     * This uses the algorithm for reading described in the paper about "FAB".
     */
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

        // TODO TODO TODO TODO SET THE REQUEST RESULT!!!

        if (!success)
        {
            result = new byte[0];
        }
        else
        {
            result = value;
        }

        requestResult = new ReadRequestResult(requestId, true, success, timestamp, result);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
    }
}


package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/*
 * This class handles write requests.
 */
public class WriteHandler
extends Handler
{
    private byte[] block;
    private SegmentGroup targets;

    /*
     * This constructor sets up the write request with a volume ID, logical offset, and data to write.
     */
    public WriteHandler(int vid, long lo, byte[] data, SegmentGroup targets, Coordinator c)
    {
        volumeId = vid;
        logicalOffset = lo;
        block = data;
        coordinator = c;
        this.targets = targets;
    }

    /*
     * This method performs a write request, as configured in the constructor.
     * At any point where the request may block on IO, blocking will cause the handler
     * to pause and execution will go to the next request handler in the queue.
     * This uses the algorithm for writing described in the paper about "FAB".
     */
    public void action()
    {
        Date currentTime = coordinator.getTimestamp();

        int orderId = coordinator.getServer().issueOrderRequest(targets, volumeId, logicalOffset, currentTime);

        boolean waiting = true;
        boolean success = false;
        while (waiting)
        {
            List<OrderRequestResult> results = coordinator.getServer().getOrderRequestResults(orderId);
            int completed = 0;
            int successful = 0;

            if (results == null)
            {
                results = new ArrayList<OrderRequestResult>();
            }

            for (OrderRequestResult each : results)
            {
                if (each.isDone())
                {
                    ++completed;

                    if (each.wasSuccessful())
                    {
                        ++successful;
                    }
                }
            }

            if (successful >= coordinator.getQuorumSize())
            {
                waiting = false;
                success = true;
            }
            else if (completed == coordinator.getSegmentGroupSize())
            {
                waiting = false;
                success = false;
            }
        }

        if (!success)
        {
            finished = true;
            return;
        }

        int writeId = coordinator.getServer().issueWriteRequest(targets, volumeId, logicalOffset, block, currentTime);

        waiting = true;
        success = false;
        while (waiting)
        {
            List<WriteRequestResult> results = coordinator.getServer().getWriteRequestResults(writeId);
            int completed = 0;
            int successful = 0;

            if (results == null)
            {
                results = new ArrayList<WriteRequestResult>();
            }

            for (WriteRequestResult each : results)
            {
                if (each.isDone())
                {
                    ++completed;

                    if (each.wasSuccessful())
                    {
                        ++successful;
                    }
                }
            }

            if (successful >= coordinator.getQuorumSize())
            {
                waiting = false;
                success = true;
            }
            else if (completed == coordinator.getSegmentGroupSize())
            {
                waiting = false;
                success = false;
            }
        }

        requestResult = new WriteRequestResult(requestId, true, success);
        coordinator.setRequestResult(requestId, (RequestResult)requestResult);
        finished = true;
    }
}



package com.virtualdisk.coordinator;

import com.virtualdisk.network.request.OrderRequestResult;
import com.virtualdisk.network.request.WriteRequestResult;

import java.util.*;

/*
 * This class handles write requests.
 */
public class WriteHandler extends Handler
{
    private byte[] block;

    /*
     * This constructor sets up the write request with a volume ID, logical offset, and data to write.
     */
    public WriteHandler(int vid, long lo, byte[] data, Coordinator c)
    {
        volumeId = vid;
        logicalOffset = lo;
        block = data;
        coordinator = c;
    }

    /*
     * Kicks off the write, required by Thread.
     */
    public void run()
    {
        write();
    }

    /*
     * This method performs a write request, as configured in the constructor.
     * At any point where the request may block on IO, blocking will cause the handler
     * to pause and execution will go to the next request handler in the queue.
     * This uses the algorithm for writing described in the paper about "FAB".
     */
    public void write()
    {
        Date currentTime = coordinator.getNewTimestamp();

        SegmentGroup targets = coordinator.getSegmentGroup(volumeId, logicalOffset);

        int orderId = coordinator.server.issueOrderRequest(targets, volumeId, logicalOffset, currentTime);

        boolean waiting = true;
        boolean success = false;
        while (waiting)
        {
            List<OrderRequestResult> results = coordinator.server.getOrderRequestResults(orderId);
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

            if (successful >= coordinator.quorumSize)
            {
                waiting = false;
                success = true;
            }
            else if (completed == coordinator.segmentGroupSize)
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

        int writeId = coordinator.server.issueWriteRequest(targets, volumeId, logicalOffset, block, currentTime);

        waiting = true;
        success = false;
        while (waiting)
        {
            List<WriteRequestResult> results = coordinator.server.getWriteRequestResults(writeId);
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

            if (successful >= coordinator.quorumSize)
            {
                waiting = false;
                success = true;
            }
            else if (completed == coordinator.segmentGroupSize)
            {
                waiting = false;
                success = false;
            }
        }

        finished = true;

        coordinator.writeResultMap.put(requestId, success);
        coordinator.requestCompletionMap.put(requestId, true);
    }
}



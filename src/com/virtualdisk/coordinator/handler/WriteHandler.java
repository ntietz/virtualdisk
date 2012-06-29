package com.virtualdisk.coordinator.handler;

import com.virtualdisk.coordinator.*;
import com.virtualdisk.network.request.*;
import com.virtualdisk.network.request.base.*;

import java.util.*;

/**
 * The WriteHandler creates, issues, and manages write requests and their results.
 */
public class WriteHandler
extends Handler
{
    private byte[] block;
    private SegmentGroup targets;

    /**
     * Standard constructor.
     * @param   volumeId        the volume we are writing to
     * @param   logicalOffset   the logical location of the write
     * @param   block           the block of data we want to write
     * @param   targets         the nodes we are writing to
     * @param   coordinator     the coordinator for the request
     */
    public WriteHandler( int volumeId
                       , long logicalOffset
                       , byte[] block
                       , SegmentGroup targets
                       , Coordinator coordinator
                       )
    {
        this.volumeId = volumeId;
        this.logicalOffset = logicalOffset;
        this.block = block;
        this.coordinator = coordinator;
        this.targets = targets;
    }

    /**
     * This action issues the WriteRequest and waits to get a response.
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
            requestResult = new WriteRequestResult(requestId, true, false);
            coordinator.setRequestResult(requestId, (RequestResult)requestResult);
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
    }
}



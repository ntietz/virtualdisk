package edu.kent.cs.virtualdisk.coordinator;

import edu.kent.cs.virtualdisk.network.*;

import java.util.*;

/*
 * This class handles write requests.
 */
public class WriteHandler extends Thread
{
    private boolean finished = false;
    private boolean result = false;
    private boolean paused = true;

    private int requestId;

    private Integer volumeId;
    private Integer logicalOffset;
    private byte[] block;

    private Coordinator coordinator;

    /*
     * This constructor sets up the write request with a volume ID, logical offset, and data to write.
     */
    public WriteHandler(Integer volume, Integer offset, byte[] data, Coordinator c)
    {
        volumeId = volume;
        logicalOffset = offset;
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
     * Returns whether the write has finished or is in progress.
     */
    public boolean getFinished()
    {
        return finished;
    }

    /*
     * Returns the result (success or failure) of the write request.
     */
    public boolean getResult()
    {
        return result;
    }

    /*
     * Setter for the requestId field.
     */
    public void setRequestId(int id)
    {
        requestId = id;
    }

    /*
     * Getter for the requestId field.
     */
    public int getRequestId()
    {
        return requestId;
    }

    /*
     * This method indicates whether the handler is paused or not.
     */
    public boolean isPaused()
    {
        return paused;
    }

    /*
     * This method sets the appropriate flags and then pauses the execution of the thread.
     * The thread will resume execution when the notify method is used.
     */
    private void pause()
    {
        paused = true;
        try
        {
            synchronized(this)
            {
                wait();
            }
        }
        catch (InterruptedException t)
        {
            //...
        }
        paused = false;
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

        pause();

        Integer orderId = coordinator.server.issueOrderRequest(targets, volumeId, logicalOffset, currentTime);

        boolean waiting = true;
        boolean success = false;
        while (waiting)
        {
            List<OrderRequestResult> results = coordinator.server.getOrderRequestResults(orderId);
            Integer completed = 0;
            Integer successful = 0;

            if (results == null)
            {
                results = new ArrayList<OrderRequestResult>();
            }

            for (OrderRequestResult each : results)
            {
                if (each.completed())
                {
                    ++completed;

                    if (each.successful())
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
            else if (results.size() == coordinator.segmentGroupSize)
            {
                waiting = false;
                success = false;
            }
            else
            {
                pause();
            }
        }

        if (!success)
        {
            finished = true;
            result = false;
            return;
        }

        Integer writeId = coordinator.server.issueWriteRequest(targets, volumeId, logicalOffset, block, currentTime);

        waiting = true;
        success = false;
        while (waiting)
        {
            List<WriteRequestResult> results = coordinator.server.getWriteRequestResults(writeId);
            Integer completed = 0;
            Integer successful = 0;

            if (results == null)
            {
                results = new ArrayList<WriteRequestResult>();
            }

            for (WriteRequestResult each : results)
            {
                if (each.completed())
                {
                    ++completed;

                    if (each.successful())
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
            else if (results.size() == coordinator.segmentGroupSize)
            {
                waiting = false;
                success = false;
            }
            else
            {
                pause();
            }
        }

        finished = true;

        if (!success)
        {
            result = false;
        }
        else
        {
            result = true;
        }

        pause();
    }
}



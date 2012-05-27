package edu.kent.cs.virtualdisk.coordinator;

import edu.kent.cs.virtualdisk.network.*;

import java.util.*;

/*
 * This class handles read requests.
 */
public class ReadHandler extends Thread
{
    private boolean finished = false;
    private byte[] result = null;
    private boolean paused = true;

    private int requestId;

    private Integer volumeId;
    private Integer logicalOffset;

    private Coordinator coordinator;

    /*
     * Constructor takes in volume ID and logical offset to configure the read request.
     */
    public ReadHandler(Integer vid, Integer lo, Coordinator c)
    {
        volumeId = vid;
        logicalOffset = lo;
        coordinator = c;
    }

    /*
     * Overloaded for Thread interface. Simply calls the read method.
     */
    public void run()
    {
        read();
    }

    /*
     * This method returns the finished status of the handler.
     */
    public boolean getFinished()
    {
        return finished;
    }

    /*
     * This method returns the result of the read request.
     */
    public byte[] getResult()
    {
        return result;
    }

    /*
     * This method is used to set the request ID for the read request.
     */
    public void setRequestId(int id)
    {
        requestId = id;
    }

    /*
     * This method is used to get the request ID for the read request.
     */
    public int getRequestId()
    {
        return requestId;
    }

    /*
     * This method is used to check whether the handler's execution is paused or not.
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
        catch (Throwable t)
        {
            //...
        }
        paused = false;
    }

    /*
     * This method performs a read request, as configured in the constructor.
     * At any point where the request may block on IO, blocking will cause the handler
     * to pause and execution will go to the next request handler in the queue.
     * This uses the algorithm for reading described in the paper about "FAB".
     */
    public void read()
    {
        SegmentGroup targets = coordinator.getSegmentGroup(volumeId, logicalOffset);

        pause();

        Integer orderId = coordinator.server.issueReadRequest(targets, volumeId, logicalOffset);

        boolean waiting = true;
        boolean success = false;
        boolean timestampsMatch = true;
        byte[] value = null;

        while (waiting)
        {
            List<ReadRequestResult> results = coordinator.server.getReadRequestResults(orderId);
            Integer completed = 0;
            Date timestamp = null;

            for (ReadRequestResult each : results)
            {
                if (each.completed())
                {
                    ++completed;

                    if (timestamp == null)
                    {
                        timestamp = each.getTimestamp();
                        value = each.getResult();
                    }

                    if ( !each.getTimestamp().equals(timestamp) )
                    {
                        timestampsMatch = false;

                        if (each.getTimestamp().after(timestamp))
                        {
                            timestamp = each.getTimestamp();
                            value = each.getResult();
                        }
                    }
                }
            }

            if (completed >= coordinator.quorumSize)
            {
                waiting = false;

                if (timestampsMatch)
                {
                    success = true;
                }
                else
                {
                    Integer id = coordinator.write(volumeId, logicalOffset, value);
                    while (!coordinator.writeCompleted(id))
                    {
                        pause();
                    }
                }
            }
            else
            {
                pause();
            }
        }

        finished = true;

        if (!success)
        {
            result = null;
        }
        else
        {
            result = value;
        }

        pause();
    }
}


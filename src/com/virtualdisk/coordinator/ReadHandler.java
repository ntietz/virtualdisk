package com.virtualdisk.coordinator;

import com.virtualdisk.network.request.ReadRequestResult;

import java.util.*;

/*
 * This class handles read requests.
 */
public class ReadHandler extends Handler
{
    private byte[] result = null;

    /*
     * Constructor takes in volume ID and logical offset to configure the read request.
     */
    public ReadHandler(int vid, long lo, Coordinator c)
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
    public void read()
    {
        SegmentGroup targets = coordinator.getSegmentGroup(volumeId, logicalOffset);

        pause();

        int orderId = coordinator.server.issueReadRequest(targets, volumeId, logicalOffset);

        boolean waiting = true;
        boolean success = false;
        boolean timestampsMatch = true;
        byte[] value = null;

        while (waiting)
        {
            List<ReadRequestResult> results = coordinator.server.getReadRequestResults(orderId);
            int completed = 0;
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
                    int id = coordinator.write(volumeId, logicalOffset, value);
                    while (!coordinator.writeCompleted(id))
                    {
                        pause();
                    }
                    
                    boolean writeSuccess = coordinator.writeResult(id);
                    timestampsMatch = writeSuccess;
                    success = writeSuccess;
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


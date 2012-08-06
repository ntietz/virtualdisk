package com.virtualdisk.network.util;

/*
 * This class exists to define a comparator, so that datanodes (with their statuses)
 * can be ordered in a priority queue for the sake of generating segment groups.
 */
public class DataNodeStatusPair implements Comparable<DataNodeStatusPair>
{
    protected DataNodeIdentifier datanode;
    protected DataNodeStatus status;

    public DataNodeStatusPair(DataNodeIdentifier d, DataNodeStatus s)
    {
        datanode = d;
        status = s;
    }

    /*
     * This simple implementation of compareTo says A < B if A is less full than B.
     */
    public int compareTo(DataNodeStatusPair other)
    {
        long difference = status.getSegmentsStored() - other.getStatus().getSegmentsStored();

        if (difference < 0)
        {
            return -1;
        }
        else if (difference == 0)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    public DataNodeIdentifier getIdentifier()
    {
        return datanode;
    }

    public DataNodeStatus getStatus()
    {
        return status;
    }

    public String toString()
    {
        return datanode.toString() + ", " + status.toString();
    }
}


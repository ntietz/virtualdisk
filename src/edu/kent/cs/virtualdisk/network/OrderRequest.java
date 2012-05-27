package edu.kent.cs.virtualdisk.network;

import java.util.*;

public class OrderRequest
implements Request, Sendable
{
    private int volumeId;
    private int logicalOffset;
    private Date timestamp;

    public OrderRequest(int v, int l, Date t)
    {
        volumeId = v;
        logicalOffset = l;
        timestamp = t;
    }

    public byte messageType()
    {
        return Sendable.orderRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }
}


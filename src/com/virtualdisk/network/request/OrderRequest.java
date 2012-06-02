package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

import java.util.*;

public class OrderRequest
extends Request
implements Sendable
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


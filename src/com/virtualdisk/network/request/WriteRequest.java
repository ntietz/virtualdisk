package com.virtualdisk.network.request;

import com.virtualdisk.network.Sendable;

import java.util.*;

public class WriteRequest
extends Request
implements Sendable
{
    private int volumeId;
    private int logicalOffset;
    private byte[] block;
    private Date timestamp;

    public WriteRequest(int v, int l, byte[] b, Date t)
    {
        volumeId = v;
        logicalOffset = l;
        block = b;
        timestamp = t;
    }

    public byte messageType()
    {
        return Sendable.writeRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }

    public byte[] getBlock()
    {
        return block;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }
}
